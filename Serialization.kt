package common

import com.google.gson.*
import com.google.gson.internal.Streams
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 * It allows to register hierarchy serialization with exact type as json field:
 * class A();
 * class B():A()
 * class C():A()
 * GsonBuilder.registerFactory(
 *      ThisTypeAdapterFactory.of(A.class)
 *          .registerSubtype(B.class, "B")
 *          .registerSubtype(C.class, "C")
 * gson.toJson(new C()) # {"type":"C"} instead of {} as A
 *
 * Taken from https://github.com/google/gson/tree/master/extras
 * That can be takes as dep gson-extras:0.2.2
 * However its official publication is broken due to broken .pom
 * And it's funtionality is crippled by needing to put Base type explicitely every time one wants to serialize something.
 * There is PR https://github.com/google/gson/pull/1448 that fixes the cripple but it's dead
 * So I just copied this factory from repo, converted to kotlin and fixed the issue.
 *
 * Now instead of doing gson.toJson(ChildValue, Parent.class) one can just do gson.toJson(ChildValue) and get proper result.
 */
class HierarchyTypeAdapterFactory<T> private constructor(
        baseType: Class<*>?,
        typeFieldName: String?,
        maintainType: Boolean
) : TypeAdapterFactory {
    private val baseType: Class<*>
    private val typeFieldName: String
    private val labelToSubtype: MutableMap<String, Class<*>> = LinkedHashMap()
    private val subtypeToLabel: MutableMap<Class<*>, String> = LinkedHashMap()
    private val maintainType: Boolean
    /**
     * Registers `type` identified by `label`. Labels are case
     * sensitive.
     *
     * @throws IllegalArgumentException if either `type` or `label`
     * have already been registered on this type adapter.
     */
    /**
     * Registers `type` identified by its [simple][Class.getSimpleName]. Labels are case sensitive.
     *
     * @throws IllegalArgumentException if either `type` or its simple name
     * have already been registered on this type adapter.
     */
    @JvmOverloads
    fun registerSubtype(type: Class<out T>?, label: String? = type!!.simpleName): HierarchyTypeAdapterFactory<T> {
        if (type == null || label == null) {
            throw NullPointerException()
        }
        require(!(subtypeToLabel.containsKey(type) || labelToSubtype.containsKey(label))) { "types and labels must be unique" }
        labelToSubtype[label] = type
        subtypeToLabel[type] = label
        return this
    }

    override fun <R> create(gson: Gson, type: TypeToken<R>): TypeAdapter<R>? {
        if (!baseType.isAssignableFrom(type.rawType)) {
            return null
        }
        val labelToDelegate: MutableMap<String, TypeAdapter<*>> = LinkedHashMap()
        val subtypeToDelegate: MutableMap<Class<*>, TypeAdapter<*>> = LinkedHashMap()
        for ((key, value) in labelToSubtype) {
            val delegate = gson.getDelegateAdapter(this, TypeToken.get(value))
            labelToDelegate[key] = delegate
            subtypeToDelegate[value] = delegate
        }
        return object : TypeAdapter<R>() {
            @Throws(IOException::class)
            override fun read(`in`: JsonReader): R {
                val jsonElement = Streams.parse(`in`)
                val labelJsonElement: JsonElement?
                labelJsonElement = if (maintainType) {
                    jsonElement.asJsonObject[typeFieldName]
                } else {
                    jsonElement.asJsonObject.remove(typeFieldName)
                }
                if (labelJsonElement == null) {
                    throw JsonParseException(
                            "cannot deserialize " + baseType
                                    + " because it does not define a field named " + typeFieldName
                    )
                }
                val label = labelJsonElement.asString
                val delegate// registration requires that subtype extends T
                        = labelToDelegate[label] as TypeAdapter<R>?
                        ?: throw JsonParseException(
                                "cannot deserialize " + baseType + " subtype named "
                                        + label + "; did you forget to register a subtype?"
                        )
                return delegate.fromJsonTree(jsonElement)
            }

            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: R) {
                val srcType: Class<*> = value!!.javaClass//::class.java
                val label = subtypeToLabel[srcType]
                val delegate// registration requires that subtype extends T
                        = subtypeToDelegate[srcType] as TypeAdapter<R>?
                        ?: throw JsonParseException(
                                "cannot serialize " + srcType.name
                                        + "; did you forget to register a subtype?"
                        )
                val jsonObject = delegate.toJsonTree(value).asJsonObject
                if (maintainType) {
                    Streams.write(jsonObject, out)
                    return
                }
                val clone = JsonObject()
                if (jsonObject.has(typeFieldName)) {
                    throw JsonParseException(
                            "cannot serialize " + srcType.name
                                    + " because it already defines a field named " + typeFieldName
                    )
                }
                clone.add(typeFieldName, JsonPrimitive(label))
                for ((key, value1) in jsonObject.entrySet()) {
                    clone.add(key, value1)
                }
                Streams.write(clone, out)
            }
        }.nullSafe()
    }

    companion object {
        /**
         * Creates a new runtime type adapter using for `baseType` using `typeFieldName` as the type field name. Type field names are case sensitive.
         * `maintainType` flag decide if the type will be stored in pojo or not.
         */
        fun <T> of(baseType: Class<T>?, typeFieldName: String?, maintainType: Boolean): HierarchyTypeAdapterFactory<T> {
            return HierarchyTypeAdapterFactory(baseType, typeFieldName, maintainType)
        }

        /**
         * Creates a new runtime type adapter using for `baseType` using `typeFieldName` as the type field name. Type field names are case sensitive.
         */
        fun <T> of(baseType: Class<T>?, typeFieldName: String?): HierarchyTypeAdapterFactory<T> {
            return HierarchyTypeAdapterFactory(baseType, typeFieldName, false)
        }

        /**
         * Creates a new runtime type adapter for `baseType` using `"type"` as
         * the type field name.
         */
        fun <T> of(baseType: Class<T>?): HierarchyTypeAdapterFactory<T> {
            return HierarchyTypeAdapterFactory(baseType, "type", false)
        }
    }

    init {
        if (typeFieldName == null || baseType == null) {
            throw NullPointerException()
        }
        this.baseType = baseType
        this.typeFieldName = typeFieldName
        this.maintainType = maintainType
    }
}

object Serialization {
    val gson = GsonBuilder()
            .registerTypeAdapterFactory(ErrorAdapter.factory)
            .registerTypeAdapterFactory(RoleAdapter.factory)
            .create()

    /**
     * Can serialize any object to json string, just pass it as param
     */
    fun toJson(obj: Any) = gson.toJson(obj)

    /**
     * Can deserialize Json string if proper Class is passed, for hierarchies Base Parent should be passed:
     *  val BadRequestString = '{"type":"BadRequest","message":"adasd"}'
     *  fromJson(BadRequestString, Error::class.java) # BadRequest(message=adasd)
     */
    fun <T>fromJson(text: String, clazz: Class<T>):T = gson.fromJson(text, clazz)
}
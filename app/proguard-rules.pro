# Tilefold — release shrinking rules.
#
# The app has no reflection-based frameworks (no Room, no Retrofit, no DI
# codegen), so these rules stay deliberately small. kotlinx.serialization is used
# from phase 3; its own consumer rules ship inside the library, but models that
# are serialized keep their generated serializers by name.

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

# Keep @Serializable models and their generated serializers.
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class io.github.pabi277.tilefold.**$$serializer { *; }
-keepclassmembers class io.github.pabi277.tilefold.** {
    *** Companion;
}
-keepclasseswithmembers class io.github.pabi277.tilefold.** {
    kotlinx.serialization.KSerializer serializer(...);
}

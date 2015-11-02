# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontobfuscate

-dontwarn com.facebook.**
-dontwarn com.parse.**
-dontwarn org.apache.**
-dontwarn org.bouncycastle.**
-dontwarn com.dropbox.client2.**

-keep class com.facebook.** { *; }
-keep class com.parse.** { *; }
-keep class org.apache.** { *; }
-keep class org.bouncycastle.** { *; }
-keep class com.dropbox.client2.** { *; }

-keep class android.support.v7.widget.SearchView { *; }

-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }
-dontwarn android.support.design.**

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

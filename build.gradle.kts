// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript{
    dependencies{
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
    }
}
plugins {
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}


ext {
    // App dependencies
    var androidXVersion = "1.0.0"
    var androidXTestCoreVersion = "1.4.0"
    var androidXTestExtKotlinRunnerVersion = "1.1.3"
    var androidXAnnotations = "1.3.0"
    var appCompatVersion = "1.4.0"
    var archLifecycleVersion = "2.4.0"
    var archTestingVersion = "2.1.0"
    var coroutinesVersion = "1.5.2"
    var cardVersion = "1.0.0"
    var dexMakerVersion = "2.12.1"
    var espressoVersion = "3.4.0"
    var fragmentKtxVersion = "1.4.0"
    var hamcrestVersion = "1.3"
    var junitVersion = "4.13.2"
    var materialVersion = "1.4.0"
    var recyclerViewVersion = "1.2.1"
    var robolectricVersion = "4.5.1"
    var rulesVersion = "1.0.1"
    var swipeRefreshLayoutVersion = "1.1.0"
    var timberVersion = "4.7.1"
    var truthVersion = "1.1.2"
}
val pluginGroup: String by project
val pluginVersion: String by project
val ideaVersion: String by project
val javaTargetVersion: String by project

plugins {
    id("org.jetbrains.intellij") version "0.4.22"
    java
    id("org.jetbrains.grammarkit") version "2020.1.1"
}

group = pluginGroup
version = pluginVersion

repositories {
    mavenCentral()
}

dependencies {
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = ideaVersion
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

sourceSets.main {
    java.srcDirs("src/main/gen")
}

tasks {
    compileJava {
        dependsOn("generateSources")
    }
    clean {
        delete("src/main/gen")
    }
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
      Add change notes here.<br>
      <em>most HTML tags may be used</em>""")
}

/** Custom tasks **/
tasks.register("generateSources") {
    dependsOn("generateParser", "generateLexer")
}

/** Parser **/
tasks.register<org.jetbrains.grammarkit.tasks.GenerateParser>("generateParser"){
    source = "src/main/resources/bnf/Latex.bnf"
    targetRoot = "src/main/gen"
    pathToParser = "/mobi/hsz/idea/latex/parser/LatexParser.java"
    pathToPsiRoot = "/mobi/hsz/idea/latex/psi"
    purgeOldFiles = true
}

tasks.register<org.jetbrains.grammarkit.tasks.GenerateLexer>("generateLexer"){
    source = "src/main/java/mobi/hsz/idea/latex/lexer/Latex.flex"
    targetDir = "src/main/gen/mobi/hsz/idea/latex/lexer/"
    targetClass = "LatexLexer"
    skeleton = "src/main/resources/lexer/idea-flex.skeleton"
    purgeOldFiles = true
}

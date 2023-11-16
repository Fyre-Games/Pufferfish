/**
 * JetBrains Space Automation
 * This Kotlin script file lets you automate build activities
 * For more info, see https://www.jetbrains.com/help/space/automation.html
 */
job(name = "Build and Publish") {

    container(displayName = "compileAndPublishAll",image = "amazoncorretto:17") {

        kotlinScript{api ->
            api.gradlew(":publishObfuscatedPublicationToFyreRepository")
        }

    }

}
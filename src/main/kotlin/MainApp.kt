package main.kotlin

import main.kotlin.domain.URL
import main.kotlin.service.SiteCrawler
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
fun main(args: Array<String>) {
    val logger: Logger = LogManager.getLogger()
    validateInput(args[0])?.let { startingDomain ->
        logger.info("Crawler started with target domain $startingDomain")
        val (visited, duration) = measureTimedValue { SiteCrawler(startingDomain).visit() }
        logger.info(
            "Crawling $startingDomain took ${duration.toString(DurationUnit.SECONDS, 3)} with $visited sites crawled",
        )
    } ?: logger.error("Invalid url: please provide a valid HTTP or HTTPS url")

}

private fun validateInput(urlParam: String): URL? =
    if(!urlParam.startsWith("http")) null
    else URL(urlParam)

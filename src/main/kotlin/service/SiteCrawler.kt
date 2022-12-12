package main.kotlin.service

import kotlinx.coroutines.Dispatchers
import main.kotlin.domain.URL
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class SiteCrawler(private val domainUrl: URL): CoroutineDispatcher<URL> {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    private var visited: Set<URL> = setOf()

    private val domainRegex =
        "^((http|https)://)?(${this.domainUrl})([//])?([\\w+//;,?:@&=+\$-_.!~*'()#'|])*?"
            .toRegex()

    fun visit() = crawlSite(this.domainUrl).run { visited.size }

    private fun crawlSite(subdomain: URL) {
        if(hasToBeCrawled(subdomain)){
            retrieveLinks(subdomain).map { url ->
                launchCoroutine(Dispatchers.IO, url, ::crawlSite)
            }
        } else logger.takeIf { logger.isTraceEnabled }?.trace("Skip crawling $subdomain")
    }

    private fun retrieveLinks(subdomain: URL) =
        HTMLParsingService().extractLinks(subdomain).also { links ->
            logger.info("Crawling $subdomain: got these href: $links")
            visited = visited.plusElement(subdomain)
        }

    private fun hasToBeCrawled(url: URL) = (!alreadyVisited(url)) && url.inDomain()

    private fun alreadyVisited(url: URL): Boolean = visited.find { it == url } != null

    private fun URL.inDomain() = this@inDomain.url.matches(domainRegex)
}

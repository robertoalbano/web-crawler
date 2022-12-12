package main.kotlin.service

import main.kotlin.domain.JSOUP_CONN_TIMEOUT
import main.kotlin.domain.JSOUP_USER_AGENT
import main.kotlin.domain.URL
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jsoup.Jsoup
import org.jsoup.UnsupportedMimeTypeException
import org.jsoup.nodes.Document
import java.net.MalformedURLException

class HTMLParsingService {

    private val logger: Logger = LogManager.getLogger(this::class.java)

    /**
     * Extracts all href within an HTML page
     */
    fun extractLinks(domain: URL): List<URL> =
        connect(domain)
            ?.select("a")
            ?.eachAttr("href")
            ?.map { link -> URL(link) }
            ?: listOf() // no urls if got exception

    /**
     * Jsoup lib call: connects and retrieves HTML document.
     * Null is returned if any errors occur
     */
    private fun connect(domain: URL): Document? = try {
        Jsoup.connect(domain.url)
            .userAgent(JSOUP_USER_AGENT)
            .timeout(JSOUP_CONN_TIMEOUT)
            .get()
    } catch(ex: UnsupportedMimeTypeException) { // filtering out refs to files (pdf, etc..)
        logger.error("Mime type error while connecting to url $domain: ${ex.message}")
            .also { logStackTrace(ex) }
            .run { null }
    }
    catch(e: MalformedURLException) {
        logger.error("Error connecting to url $domain: ${e.message}")
            .also { logStackTrace(e) }
            .run { null }
    }

    private fun logStackTrace(ex: Exception) =
        logger.takeIf { logger.isTraceEnabled }?.trace(ex.printStackTrace())
}
package test.kotlin.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import main.kotlin.domain.URL
import main.kotlin.service.HTMLParsingService
import main.kotlin.service.SiteCrawler
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SiteCrawlerTest {

    @MockK
    private val htmlParsingMock: HTMLParsingService = mockk()

    @InjectMockKs
    lateinit var crawler: SiteCrawler

    @Test
    fun testHappyPath(){
        val domain = URL("https://www.dorbit.space")
        every { htmlParsingMock.extractLinks(any()) } returns listOf()
        val res = SiteCrawler(domain).visit()
        assertEquals(0, res)
    }

    @Test
    fun outOfDomainLinksAreNotVisited(){
        val domain = URL("https://www.dorbit.space")
        every { htmlParsingMock.extractLinks(any()) } returns listOf(
            URL("https://www.dorbit.space/our-solutions"),
            URL("https://www.dorbit.space/aurora"),
            URL("www.facebook.com/d-orbit"), // not in domain
        )
        val res = SiteCrawler(domain).visit()
        assertEquals(2, res)
    }

}

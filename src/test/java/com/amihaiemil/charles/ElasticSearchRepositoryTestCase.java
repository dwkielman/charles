/*
 Copyright (c) 2016, Mihai Emil Andronache
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.
 * Neither the name of charles nor the names of its
 contributors may be used to endorse or promote products derived from
 this software without specific prior written permission.
 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.amihaiemil.charles;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test cases for {@link ElasticSearchRepository}
 * @author Mihai Andronache (amihaiemil@gmail.com)
 *
 */
public class ElasticSearchRepositoryTestCase {
	
	
	/**
	 * {@link ElasticSearchRepository} can send the given list of json docs
	 * to the specified elastisearch index.
	 * @throws Exception - If something goes wrong.
	 */
	@Test
    public void indexesListOfDocuments() throws Exception {
		CloseableHttpClient httpClient = Mockito.mock(CloseableHttpClient.class);
		CloseableHttpResponse httpResponse = Mockito.mock(CloseableHttpResponse.class);
		HttpEntity responseContentEntity = Mockito.mock(HttpEntity.class);
		StatusLine responseStatusLine = Mockito.mock(StatusLine.class);
		Mockito.when(responseStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
		Mockito.when(responseContentEntity.getContent()).thenReturn(this.mockElasticIndexResponse());
		Mockito.when(httpResponse.getEntity()).thenReturn(responseContentEntity);
		Mockito.when(httpResponse.getStatusLine()).thenReturn(responseStatusLine);
		
		Mockito.when(httpClient.execute(Mockito.any(HttpPost.class))).thenReturn(httpResponse);
		List<WebPage> pages = new ArrayList<WebPage>();
		pages.add(this.webPage("http://www.amihaiemil.com/index.html"));
		pages.add(this.webPage("http://eva.amihaiemil.com/index.html"));
    	ElasticSearchRepository elasticRepo = new ElasticSearchRepository(
    	    "http://localhost:9200/test5", httpClient
    	);
    	elasticRepo.export(pages);
    }
	
	public InputStream mockElasticIndexResponse() throws FileNotFoundException, IOException {
		return new ByteArrayInputStream(
			IOUtils.toByteArray(
				new FileInputStream(
					new File("src/test/resources/elasticIndexResponse.json")
				)
			)
		);
	}

	/**
	 * Returns a WebPage.
	 * @param url URL of the page.
	 * @return WebPage
	 */
	public WebPage webPage(String url) {
		WebPage page = new SnapshotWebPage();
		page.setUrl(url);
		page.setLinks(new LinkedHashSet<Link>());
		page.setName("indextest.html");
		page.setTitle("Intex Test | Title");
		page.setTextContent("Test content of this awesome test page.");
	    page.setCategory("page");
		return page;
	}

}

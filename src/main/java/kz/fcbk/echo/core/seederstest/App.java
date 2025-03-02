package kz.fcbk.echo.core.seederstest;

import kz.fcbk.echo.core.seederstest.config.OkHttpClientProvider;
import kz.fcbk.echo.core.seederstest.service.ResultWriterService;
import kz.fcbk.echo.core.seederstest.service.UrlRequestAndRotate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
@Slf4j
public class App implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<String> urls = List.of(
				"https://abouthealthtransparency.org/category/canada/",
				"http://soltustikkaz.kz/index.php/sport",
				"https://spik.kz/issledovaniya",
				"http://talks.su/chapters/criminal/",
				"https://aqmolanews.kz/ru/category/eksklyuziv/",
				"http://asylsoz.kz/kk/",
				"https://atyrauaqparat.kz/category/%D2%9B%D2%B1%D2%9B%D1%8B%D2%9B/",
				"http://avtoradio.kz/programms/persona-contra/",
				"http://gazetavesti.kz/index.php/component/k2/itemlist/category/7-%D0%B7%D0%B0%D0%BA%D0%BE%D0%BD-%D0%B8-%D0%BF%D0%BE%D1%80%D1%8F%D0%B4%D0%BE%D0%BA",
				"https://gharysh.kz/news/",
				"https://www.greenqazaqstan.kz/category/news/",
				"http://iap.kz/kz/news-kz?start=8",
				"http://www.adilsoz.kz/politcor/index",
				"https://kaztag.kz/kz/expert-opinion/",
				"http://kerek-info.kz/kazakhstan/",
				"http://kolesa.kz/content/articles/pdd-articles/",
				"http://kostanai.kz/articles.php"
		);


		UrlRequestAndRotate rotationService = new UrlRequestAndRotate(
				OkHttpClientProvider.createClient(true),
				urls
		);
		ResultWriterService writerService = new ResultWriterService("results.log");

		for (int i = 0; i < urls.size(); i++) {
			writerService.writeResult(rotationService.makeRequest());
		}
		log.info("Complete process URLS list.");
		System.exit(0);
	}
}

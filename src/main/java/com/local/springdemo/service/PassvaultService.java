package com.local.springdemo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.local.springdemo.model.Passvault;
import com.local.springdemo.repository.IPassvaultRepository;
import com.local.springdemo.service.PassvaultService.MyTask;

@Service
public class PassvaultService {

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	IPassvaultRepository passvaultRepository;

	public List<Passvault> getAllPassvault() {
		Iterable<Passvault> passVaultIterator = passvaultRepository.findAll();
		List<Passvault> passvaults = new ArrayList<>();
		passVaultIterator.forEach(passvaults::add);
		return passvaults;
	}

	@Async("threadPoolTaskExecutor")
	public void loadData(String dataDirectory) {
		List<Passvault> data = processInputFile(dataDirectory);
		data.forEach(vault -> {
			passvaultRepository.save(vault);
		});

	}

	private List<Passvault> processInputFile(String inputFilePath) {
		List<Passvault> inputList = new ArrayList<>();
		try {
			Files.list(Paths.get(inputFilePath)).forEach(input -> {
				try {
					try (BufferedReader br = Files.newBufferedReader(input)) {
						inputList.addAll(br.lines().skip(1).map(mapToItem).collect(Collectors.toList()));
					}
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			});

			List<MyTask> tasks = inputList.stream().map(item -> new MyTask(item)).collect(Collectors.toList());
			List<Passvault> finallist = useCompletableFutureWithExecutor(tasks);
			finallist.stream().sorted(Comparator.comparing(Passvault::getResponseCode)
					.thenComparing(Comparator.comparing(Passvault::getUrl))).collect(Collectors.toList());
			inputList.forEach(item -> {
				System.out.println(item.getUrl() + "," + item.getUsername() + "," + item.getPassword() + ","
						+ item.getStatus() + "," + item.getResponseCode());
			});

		} catch (IOException e1) {
			System.out.println(e1.getLocalizedMessage());
		}

		return inputList;

	}

	public static void useCompletableFuture(List<MyTask> tasks) {
		long start = System.nanoTime();
		List<CompletableFuture<Passvault>> futures = tasks.stream()
				.map(t -> CompletableFuture.supplyAsync(() -> t.validate())).collect(Collectors.toList());

		futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		long duration = (System.nanoTime() - start) / 1_000_000;
		System.out.printf("Processed %d tasks in %d millis\n", tasks.size(), duration);
	}

	class MyTask {
		private final Passvault item;

		public MyTask(Passvault item) {
			this.item = item;
		}

		public Passvault validate() {
			if (item.getUrl().contains("http:") || item.getUrl().contains("https:")) {
				String input = item.getUrl().replaceFirst("^https", "http");
				try {
					URL url = new URL(input);
					HttpURLConnection huc = (HttpURLConnection) url.openConnection();
					huc.setConnectTimeout(1000);
					huc.setReadTimeout(1000);
					huc.setRequestMethod("HEAD");
					int responseCode = huc.getResponseCode();
					item.setStatus(huc.getResponseMessage());
					item.setResponseCode(responseCode);
				} catch (IOException exception) {
					System.out.println(exception.getMessage());
					item.setStatus(exception.getMessage());
					item.setResponseCode(500);
				}
				return item;
			}
			item.setResponseCode(200);
			item.setStatus("OK");
			return item;
		}
	}

	public static List<Passvault> useCompletableFutureWithExecutor(List<MyTask> tasks) {
		long start = System.nanoTime();
		ExecutorService executor = Executors.newFixedThreadPool(Math.min(tasks.size(), 25));
		List<CompletableFuture<Passvault>> futures = tasks.stream()
				.map(t -> CompletableFuture.supplyAsync(() -> t.validate(), executor)).collect(Collectors.toList());

		List<Passvault> result = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		long duration = (System.nanoTime() - start) / 1_000_000;
		System.out.printf("Processed %d tasks in %d millis\n", tasks.size(), duration);
		try {
			executor.shutdown();
			executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.err.println("tasks interrupted");
		} finally {
			if (!executor.isTerminated()) {
				System.err.println("cancel non-finished tasks");
			}
			executor.shutdownNow();
		}
		return result;
	}

	private Function<String, Passvault> mapToItem = (line) -> {
		String[] p = line.split(",");
		Passvault item = new Passvault();
		char[] values = p[0].trim().toCharArray();
		if (!Character.isDigit(values[0])) {
			try {

				if (p[1].contains("android://")) {
					item.setUrl(p[0].trim());
				} else {
					item.setUrl(p[1].trim());
				}
				item.setUsername(p[2].trim());
				item.setPassword(p[3].trim());
			} catch (Exception e) {
				System.out.println(e.getMessage() + p[0]);
			}
		} else {
			try {
				item.setUrl(p[1].trim());
				item.setUsername(p[2].trim());
				item.setPassword(p[3].trim());
			} catch (Exception e) {
				System.out.println(e.getMessage() + p[0]);
			}
		}
		return item;
	};

}

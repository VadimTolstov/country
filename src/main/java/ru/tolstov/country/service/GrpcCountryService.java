package ru.tolstov.country.service;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.tolstov.country.domain.Country;
import ru.tolstov.country.domain.CountryInput;
import ru.tolstov.grpc.country.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class GrpcCountryService extends CountryServiceGrpc.CountryServiceImplBase {

    private final CountryService countryService;
    private static final Random RANDOM = new Random();

    @Autowired
    public GrpcCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public void country(IdRequest request, StreamObserver<CountryResponse> responseObserver) {
        final Country country = countryService.getCountry(UUID.fromString(request.getId()));
        responseObserver.onNext(
                CountryResponse.newBuilder()
                        .setId(country.id().toString())
                        .setName(country.name())
                        .setCode(country.code())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void countries(Empty request, StreamObserver<CountryListResponse> responseObserver) {
        // Строим единый ответ со списком стран
        CountryListResponse response = CountryListResponse.newBuilder()
                .addAllCountries(countryService.allCountriesGql().stream()
                        .map(country -> CountryResponse.newBuilder()
                                .setId(country.id().toString())
                                .setName(country.name())
                                .setCode(country.code())
                                .build())
                        .toList())
                .build();
        // Отправляем единый ответ и завершаем вызов
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void countriesPage(CountriesRequest request, StreamObserver<CountryListResponse> responseObserver) {
        try {
            // Создаем объект пагинации
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

            // Получаем страницу стран
            Page<Country> countryPage = countryService.allCountry(pageable);

            // Строим ответ
            CountryListResponse.Builder responseBuilder = CountryListResponse.newBuilder();

            for (Country country : countryPage.getContent()) {
                responseBuilder.addCountries(
                        CountryResponse.newBuilder()
                                .setId(country.id().toString())
                                .setName(country.name())
                                .setCode(country.code())
                                .build()
                );
            }

            // Добавляем метаданные пагинации в ответ
            responseBuilder
                    .setCurrentPage(countryPage.getNumber())
                    .setTotalPages(countryPage.getTotalPages())
                    .setTotalItems(countryPage.getTotalElements());

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error fetching countries: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void addCountry(CountryRequest request, StreamObserver<CountryResponse> responseObserver) {
        final Country country = countryService.addCountry(new CountryInput(
                request.getName(),
                request.getCode()
        ));
        responseObserver.onNext(
                CountryResponse.newBuilder()
                        .setId(country.id().toString())
                        .setName(country.name())
                        .setCode(country.code())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<CountryRequest> addStreamCountry(StreamObserver<StreamAddResponse> responseObserver) {
        return new StreamObserver<CountryRequest>() {
            private static final int BATCH_SIZE = 50;
            private final List<CountryInput> batch = new ArrayList<>();
            private final List<String> errors = new ArrayList<>();
            private int totalSuccess = 0;

            @Override
            public void onNext(CountryRequest request) {
                try {
                    // Проверяем валидность данных
                    if (request.getName().isEmpty() || request.getCode().isEmpty()) {
                        errors.add("Invalid request: name or code is empty");
                        return;
                    }

                    // Добавляем в текущий пакет
                    batch.add(new CountryInput(request.getName(), request.getCode()));

                    // Если пакет заполнен - обрабатываем
                    if (batch.size() >= BATCH_SIZE) {
                        processBatch();
                    }
                } catch (Exception e) {
                    errors.add("Processing error: " + e.getMessage());
                }
            }

            private void processBatch() {
                if (batch.isEmpty()) return;

                try {
                    int savedCount = countryService.addBatch(batch);
                    totalSuccess += savedCount;

                    // Если не все сохранились - добавляем ошибки
                    if (savedCount < batch.size()) {
                        int failedCount = batch.size() - savedCount;
                        errors.add(failedCount + " items failed in batch save");
                    }
                } catch (Exception e) {
                    errors.add("Batch save error: " + e.getMessage());
                } finally {
                    batch.clear();
                }
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Stream processing failed: " + t.getMessage())
                        .asRuntimeException());
            }

            @Override
            public void onCompleted() {
                // Обрабатываем последний пакет
                processBatch();

                // Формируем детализированный ответ
                StreamAddResponse response = StreamAddResponse.newBuilder()
                        .setSuccessCount(totalSuccess)
                        .addAllErrors(errors)
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();

                // Логируем результаты
                if (!errors.isEmpty()) {
                    log.error("Completed stream processing with {} errors", errors.size());
                }
            }
        };
    }

    @Override
    public void randomCountry(CountRequest request, StreamObserver<CountryResponse> responseObserver) {
        final List<Country> countryList = countryService.allCountriesGql();
        for (int i = 0; i < request.getCount(); i++) {
            Country country = countryList.get(RANDOM.nextInt(countryList.size()));
            responseObserver.onNext(
                    CountryResponse.newBuilder()
                            .setId(country.id().toString())
                            .setName(country.name())
                            .setCode(country.code())
                            .build()
            );
        }
        responseObserver.onCompleted();
    }
}

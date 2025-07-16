package ru.tolstov.country.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tolstov.country.domain.Country;
import ru.tolstov.country.domain.CountryInput;
import ru.tolstov.grpc.country.*;

import java.util.UUID;

@Service
public class GrpcCountryService extends CountryServiceGrpc.CountryServiceImplBase {

    private final CountryService countryService;

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


    }

    @Override
    public void addCountry(CountryRequest request, StreamObserver<CountryResponse> responseObserver) {
        Country country = countryService.addCountry(new CountryInput(
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
    public StreamObserver<CountryRequest> addStreamCountry(StreamObserver<CountResponse> responseObserver) {
        return super.addStreamCountry(responseObserver);
    }

    @Override
    public void randomCountry(CountRequest request, StreamObserver<CountryResponse> responseObserver) {
        super.randomCountry(request, responseObserver);
    }
}

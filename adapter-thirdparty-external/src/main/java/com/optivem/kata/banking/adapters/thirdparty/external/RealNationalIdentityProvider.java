package com.optivem.kata.banking.adapters.thirdparty.external;

import com.optivem.kata.banking.core.ports.driven.NationalIdentityProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class RealNationalIdentityProvider implements NationalIdentityProvider {
    private static final String URL = "https://jsonplaceholder.typicode.com"; // TODO: Move into configuration
    private static final String PATH = "users/%s";

    @Override
    public boolean exists(String nationalIdentityNumber) {
        var client = WebClient.create(URL);
        var path = getPath(nationalIdentityNumber);
        var responseSpec = client.get().uri(path).retrieve();

        var user = responseSpec
                .onStatus(status -> HttpStatus.NOT_FOUND.equals(status), response -> Mono.empty())
                .bodyToMono(UserDto.class)
                .block();

        return user.getId() != null;
    }

    private String getPath(String nationalIdentityNumber) {
        return String.format(PATH, nationalIdentityNumber);
    }
}
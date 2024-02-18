package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Adding a song."
    request {
        url "/songs"
        method POST()
        headers {
            contentType applicationJson()
        }
        body(
                name: "Destiny",
                artist: "Markus Schulz, Delacey",
                album: "Watch the World",
                length: "227",
                year: "2015",
                resourceId: "1"
        )
    }
    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body(1)
    }
}
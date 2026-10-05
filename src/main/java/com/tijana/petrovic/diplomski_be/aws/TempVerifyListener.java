package com.tijana.petrovic.diplomski_be.aws;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class TempVerifyListener {

    @SqsListener("${aws.sqs.document-upload-queue}")
    public void onUpload(String payload) {
        log.info("VERIFY_LISTENER_RECEIVED: {}", payload);
    }
}

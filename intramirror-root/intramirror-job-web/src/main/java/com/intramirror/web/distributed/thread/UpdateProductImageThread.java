package com.intramirror.web.distributed.thread;

import com.intramirror.web.distributed.service.ApiUpdateProductImageService;

public class UpdateProductImageThread implements Runnable {

    private ApiUpdateProductImageService apiUpdateProductImageService;

    @Override
    public void run() {
        apiUpdateProductImageService.update();
    }

    public UpdateProductImageThread(ApiUpdateProductImageService apiUpdateProductImageService) {
        this.apiUpdateProductImageService = apiUpdateProductImageService;
    }
}

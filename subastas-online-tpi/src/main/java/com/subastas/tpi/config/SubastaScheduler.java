package com.subastas.tpi.config;

import com.subastas.tpi.service.SubastaService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class SubastaScheduler {

    private final SubastaService subastaService;

    public SubastaScheduler(SubastaService subastaService){
        this.subastaService = subastaService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void actualizarEstados() {

        subastaService.actualizarEstadosAutomaticamente();

    }

}

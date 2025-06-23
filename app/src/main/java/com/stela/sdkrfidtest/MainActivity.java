
package com.stela.sdkrfidtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.grotg.hpp.otglibrary.exception.ReaderException;
import com.grotg.hpp.otglibrary.otgreader.OtgReader;
import com.grotg.hpp.otglibrary.param.BankType;
import com.grotg.hpp.otglibrary.param.EpcBean;
import com.grotg.hpp.otglibrary.param.RegionType;

public class MainActivity extends Activity {

    private OtgReader reader;
    private TextView log;
    private Button connectBtn, startScanBtn, stopScanBtn, readBtn, infoBtn;
    private Button getPowerBtn, setPowerBtn, readMemBtn, writeMemBtn, lockBtn, killBtn, setRegionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        log = findViewById(R.id.log);
        connectBtn = findViewById(R.id.connect);
        startScanBtn = findViewById(R.id.scan_start);
        stopScanBtn = findViewById(R.id.scan_stop);
        readBtn = findViewById(R.id.read_once);
        infoBtn = findViewById(R.id.info);
        getPowerBtn = findViewById(R.id.get_power);
        setPowerBtn = findViewById(R.id.set_power);
        readMemBtn = findViewById(R.id.read_mem);
        writeMemBtn = findViewById(R.id.write_mem);
        lockBtn = findViewById(R.id.lock_tag);
        killBtn = findViewById(R.id.kill_tag);


        reader = new OtgReader(this);

        reader.setreadTagDataCallback(tag -> runOnUiThread(() -> {
            log.append("Tag EPC: " + tag.strepc + " | RSSI: " + tag.intRssi + "\n");
        }));

        connectBtn.setOnClickListener(v -> reader.connect((success, msg) -> runOnUiThread(() -> {
            log.append("Conectado? " + success + " - " + msg + "\n");
        })));

        startScanBtn.setOnClickListener(v -> {
            try {
                reader.ScanTags();
            } catch (ReaderException e) {
                log.append("Erro ao iniciar leitura: " + e.getMessage() + "\n");
            }
        });

        stopScanBtn.setOnClickListener(v -> {
            try {
                reader.StopScan();
            } catch (ReaderException e) {
                log.append("Erro ao parar leitura: " + e.getMessage() + "\n");
            }
        });

        readBtn.setOnClickListener(v -> {
            try {
                EpcBean tag = reader.Read();
                log.append("Leitura única: EPC=" + tag.strepc + "\n");
            } catch (ReaderException e) {
                log.append("Erro ao ler tag: " + e.getMessage() + "\n");
            }
        });

        infoBtn.setOnClickListener(v -> {
            String info = "API: " + reader.getApiVersion() +
                    "\nHW: " + reader.getHardwareVersion() +
                    "\nSW: " + reader.getSoftwareVersion() +
                    "\nSN: " + reader.getSNnumber();
            log.append(info + "\n");
        });

        getPowerBtn.setOnClickListener(v -> {
            try {
                int power = reader.getPower();
                log.append("Potência atual: " + power + "\n");
            } catch (ReaderException e) {
                log.append("Erro ao obter potência: " + e.getMessage() + "\n");
            }
        });

        setPowerBtn.setOnClickListener(v -> {
            try {
                reader.setPower(30);
                log.append("Potência ajustada para 30\n");
            } catch (ReaderException e) {
                log.append("Erro ao ajustar potência: " + e.getMessage() + "\n");
            }
        });

        readMemBtn.setOnClickListener(v -> {
            try {
                String mem = reader.readMemory(BankType.EPC, 2, 6, "00000000");
                log.append("Memória EPC: " + mem + "\n");
            } catch (ReaderException e) {
                log.append("Erro leitura memória: " + e.getMessage() + "\n");
            }
        });

        writeMemBtn.setOnClickListener(v -> {
            try {
                reader.writeMemory(BankType.EPC, 0, 2, "ABCD", "00000000");
                log.append("Escreveu USER: ABCD\n");
            } catch (ReaderException e) {
                log.append("Erro escrita: " + e.getMessage() + "\n");
            }
        });

        lockBtn.setOnClickListener(v -> {
            try {
                reader.LockTag("00000000", "0000");
                log.append("Tag bloqueada com sucesso\n");
            } catch (ReaderException e) {
                log.append("Erro ao bloquear tag: " + e.getMessage() + "\n");
            }
        });

        killBtn.setOnClickListener(v -> {
            try {
                reader.KillTag("00000000");
                log.append("Tag destruída\n");
            } catch (ReaderException e) {
                log.append("Erro ao destruir tag: " + e.getMessage() + "\n");
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reader != null) {
            reader.destroy();
        }
    }
}

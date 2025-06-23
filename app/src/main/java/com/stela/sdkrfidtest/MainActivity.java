
package com.stela.sdkrfidtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grotg.hpp.otglibrary.exception.ReaderException;
import com.grotg.hpp.otglibrary.otgreader.OtgReader;
import com.grotg.hpp.otglibrary.param.BankType;
import com.grotg.hpp.otglibrary.param.EpcBean;
import com.grotg.hpp.otglibrary.param.RegionType;

public class MainActivity extends Activity {

    private OtgReader reader;

    private RecyclerView recyclerLog;
    private LogAdapter logAdapter;
    private Button btnClearLog;

    private Spinner spPower;
    private TextView tvPowerCurrent;

    private Button connectBtn, startScanBtn, stopScanBtn, readBtn, infoBtn, getPowerBtn, readMemBtn, writeMemBtn, lockBtn, killBtn, btnGetBuzzer;

    private Button btnApplyPower, btnRefreshPower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupReader();
        setupListeners();
        setupSpinner();
    }

    private void initViews() {
        recyclerLog = findViewById(R.id.recyclerLog);
        btnClearLog = findViewById(R.id.btnClearLog);
        spPower = findViewById(R.id.spPower);
        tvPowerCurrent = findViewById(R.id.tvPowerCurrent);

        connectBtn = findViewById(R.id.connect);
        startScanBtn = findViewById(R.id.scan_start);
        stopScanBtn = findViewById(R.id.scan_stop);
        readBtn = findViewById(R.id.read_once);
        infoBtn = findViewById(R.id.info);
        getPowerBtn = findViewById(R.id.get_power);
        readMemBtn = findViewById(R.id.read_mem);
        writeMemBtn = findViewById(R.id.write_mem);
        lockBtn = findViewById(R.id.lock_tag);
        killBtn = findViewById(R.id.kill_tag);

        btnApplyPower = findViewById(R.id.btnApplyPower);
        btnRefreshPower = findViewById(R.id.btnRefreshPower);
        btnGetBuzzer  = findViewById(R.id.btnGetBuzzer);
    }

    private void setupRecyclerView() {
        logAdapter = new LogAdapter();
        recyclerLog.setLayoutManager(new LinearLayoutManager(this));
        recyclerLog.setAdapter(logAdapter);

        btnClearLog.setOnClickListener(v -> logAdapter.clearLog());
    }

    private void setupReader() {
        reader = new OtgReader(this);
        reader.setreadTagDataCallback(tag -> runOnUiThread(() -> {
            addLog("Tag EPC: " + tag.strepc + " | RSSI: " + tag.intRssi);
        }));
    }

    private void setupListeners() {
        connectBtn.setOnClickListener(v -> connectReader());
        startScanBtn.setOnClickListener(v -> startScan());
        stopScanBtn.setOnClickListener(v -> stopScan());
        readBtn.setOnClickListener(v -> readOnce());
        infoBtn.setOnClickListener(v -> showInfo());
        getPowerBtn.setOnClickListener(v -> getPower());

        readMemBtn.setOnClickListener(v -> readMemory());
        writeMemBtn.setOnClickListener(v -> writeMemory());
        lockBtn.setOnClickListener(v -> lockTag());
        killBtn.setOnClickListener(v -> killTag());

        btnApplyPower.setOnClickListener(v -> applySelectedPower());
        btnRefreshPower.setOnClickListener(v -> refreshPower());
        btnGetBuzzer.setOnClickListener(v -> playBuzzer());
    }

    private void setupSpinner() {
        Integer[] powers = new Integer[9];
        for (int i = 0; i < powers.length; i++) powers[i] = 12 + i;
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, powers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPower.setAdapter(adapter);
    }

    private void connectReader() {
        reader.connect((success, msg) -> runOnUiThread(() ->
                addLog("Conectado? " + success + " - " + msg)
        ));
    }

    private void startScan() {
        try {
            reader.ScanTags();
        } catch (ReaderException e) {
            addLog("Erro ao iniciar leitura: " + e.getMessage());
        }
    }

    private void stopScan() {
        try {
            reader.StopScan();
        } catch (ReaderException e) {
            addLog("Erro ao parar leitura: " + e.getMessage());
        }
    }

    private void readOnce() {
        try {
            EpcBean tag = reader.Read();
            if (tag != null) {
                addLog("Leitura única: EPC=" + tag.strepc);
            } else {
                addLog("Nenhuma tag detectada.");
            }
        } catch (ReaderException e) {
            addLog("Erro ao ler tag: " + e.getMessage());
        }
    }

    private void showInfo() {
        String info = "API: " + reader.getApiVersion() +
                "\nHW: " + reader.getHardwareVersion() +
                "\nSW: " + reader.getSoftwareVersion() +
                "\nSN: " + reader.getSNnumber();
        addLog(info);
    }

    private void getPower() {
        try {
            int power = reader.getPower();
            addLog("Potência atual: " + power);
        } catch (ReaderException e) {
            addLog("Erro ao obter potência: " + e.getMessage());
        }
    }

    private void readMemory() {
        try {
            String mem = reader.readMemory(BankType.EPC, 2, 6, "00000000");
            addLog("Memória EPC: " + mem);
        } catch (ReaderException e) {
            addLog("Erro leitura memória: " + e.getMessage());
        }
    }

    private void writeMemory() {
        try {
            reader.writeMemory(BankType.EPC, 0, 2, "ABCD", "00000000");
            addLog("Escreveu USER: ABCD");
        } catch (ReaderException e) {
            addLog("Erro escrita: " + e.getMessage());
        }
    }

    private void lockTag() {
        try {
            reader.LockTag("00000000", "0000");
            addLog("Tag bloqueada com sucesso");
        } catch (ReaderException e) {
            addLog("Erro ao bloquear tag: " + e.getMessage());
        }
    }

    private void killTag() {
        try {
            reader.KillTag("00000000");
            addLog("Tag destruída");
        } catch (ReaderException e) {
            addLog("Erro ao destruir tag: " + e.getMessage());
        }
    }

    private void applySelectedPower() {
        int selected = (int) spPower.getSelectedItem();
        try {
            reader.setPower(selected);
            Toast.makeText(this, "Potência definida para " + selected, Toast.LENGTH_SHORT).show();
            refreshPower();
        } catch (ReaderException e) {
            Toast.makeText(this, "Erro ao definir potência: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void refreshPower() {
        try {
            int pw = reader.getPower();
            tvPowerCurrent.setText("Potência atual: " + pw);
        } catch (ReaderException e) {
            tvPowerCurrent.setText("Erro ao ler potência");
            e.printStackTrace();
        }
    }

    private void addLog(String message) {
        logAdapter.addLog(message);
        recyclerLog.scrollToPosition(logAdapter.getItemCount() - 1);
    }

    private void playBuzzer(){
        try {
            reader.getBuzzer();
            addLog("getBuzzer() executado com sucesso.");
        } catch (ReaderException e) {
            addLog("Erro ao executar getBuzzer(): " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reader != null) {
            reader.destroy();
        }
    }
}



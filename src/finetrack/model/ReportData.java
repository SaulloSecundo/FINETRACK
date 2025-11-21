package finetrack.model;

import java.time.YearMonth;
import java.util.Map;

public class ReportData {

    private Map<String, Double> gastosPorCategoria;
    private Map<YearMonth, Double> saldoMensal;

    public ReportData(Map<String, Double> gastosPorCategoria, Map<YearMonth, Double> saldoMensal) {
        this.gastosPorCategoria = gastosPorCategoria;
        this.saldoMensal = saldoMensal;
    }

    public Map<String, Double> getGastosPorCategoria() {
        return gastosPorCategoria;
    }

    public Map<YearMonth, Double> getSaldoMensal() {
        return saldoMensal;
    }
}

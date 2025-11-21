package finetrack.service;

import finetrack.model.*;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    private final TransactionService transactionService;

    public ReportService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    //Gera relatório completo para um mês específico.
    
    public ReportData generateMonthlyReport(YearMonth month) {
        List<Transaction> transacoesDoMes = transactionService
                .filterByMonth(month);

        Map<String, Double> gastosPorCategoria = calcularGastosPorCategoria(transacoesDoMes);
        Map<YearMonth, Double> saldoMensal = calcularEvolucaoMensal();

        return new ReportData(gastosPorCategoria, saldoMensal);
    }

    //Calcula o total gasto por categoria de despesa.
    
    public Map<String, Double> calcularGastosPorCategoria(List<Transaction> transacoes) {
        return transacoes.stream()
                .filter(t -> t.getTipo() == TransactionType.DESPESA)
                .collect(Collectors.groupingBy(
                        Transaction::getCategoria,
                        Collectors.summingDouble(Transaction::getValor)
                ));
    }

    //Calcula o saldo mensal para todos os meses existentes no sistema.
   
    public Map<YearMonth, Double> calcularEvolucaoMensal() {
        List<Transaction> todasTransacoes = transactionService.getAll();

        Map<YearMonth, Double> saldoPorMes = new TreeMap<>();

        for (Transaction t : todasTransacoes) {
            YearMonth mes = YearMonth.from(t.getData());

            saldoPorMes.putIfAbsent(mes, 0.0);

            double valorAtual = saldoPorMes.get(mes);

            if (t.getTipo() == TransactionType.RECEITA) {
                saldoPorMes.put(mes, valorAtual + t.getValor());
            } else {
                saldoPorMes.put(mes, valorAtual - t.getValor());
            }
        }

        return saldoPorMes;
    }

    // Exporta texto simples com o resumo do mês.
   
    public String gerarResumoMensal(YearMonth mes) {
        List<Transaction> transacoes = transactionService.filterByMonth(mes);

        double totalReceitas = transacoes.stream()
                .filter(t -> t.getTipo() == TransactionType.RECEITA)
                .mapToDouble(Transaction::getValor)
                .sum();

        double totalDespesas = transacoes.stream()
                .filter(t -> t.getTipo() == TransactionType.DESPESA)
                .mapToDouble(Transaction::getValor)
                .sum();

        Map<String, Double> gastosPorCategoria = calcularGastosPorCategoria(transacoes);

        StringBuilder sb = new StringBuilder();

        sb.append("Relatório Financeiro - ").append(mes).append("\n");
        sb.append("-----------------------------------------\n");
        sb.append("Total de Receitas: R$ ").append(totalReceitas).append("\n");
        sb.append("Total de Despesas: R$ ").append(totalDespesas).append("\n");
        sb.append("Saldo do Mês: R$ ").append(totalReceitas - totalDespesas).append("\n\n");

        sb.append("Gastos por Categoria:\n");
        gastosPorCategoria.forEach((cat, valor) -> {
            sb.append(" - ").append(cat).append(": R$ ").append(valor).append("\n");
        });

        return sb.toString();
    }
}

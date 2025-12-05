package ufrn.finetrack.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class HomeController {

    @FXML private Label lblTotalReceitas;
    @FXML private Label lblTotalDespesas;
    @FXML private Label lblFluxoLiquido;

    @FXML private ToggleButton toggleSituacao;
    @FXML private PieChart pieGastos;

    @FXML
    public void initialize() {

        // Valores fictícios (crie depois com ReportService)
        lblTotalReceitas.setText("R$ 5000,00");
        lblTotalDespesas.setText("R$ 3200,00");
        lblFluxoLiquido.setText("R$ 1800,00");

        pieGastos.getData().addAll(
            new PieChart.Data("Alimentos", 62.5),
            new PieChart.Data("Transporte", 25),
            new PieChart.Data("Contas fixas", 12.5)
        );
    }
    
    @FXML
    private void goToTransactions() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ufrn/finetrack/view/TransactionsView.fxml")
            );

            Scene scene = new Scene(loader.load(), 1280, 720);
            Stage stage = (Stage) lblTotalReceitas.getScene().getWindow();
            scene.getStylesheets().add(
                getClass().getResource("/ufrn/finetrack/view/style.css").toExternalForm()
            );
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package ufrn.finetrack.controller;

import java.util.Arrays;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ufrn.finetrack.model.ExpenseCategory;
import ufrn.finetrack.model.IncomeCategory;
import ufrn.finetrack.model.Transaction;
import ufrn.finetrack.model.TransactionType;
import ufrn.finetrack.service.TransactionService;

public class TransactionsController {

    @FXML private TableView<Transaction> tableTransacoes;
    @FXML private ChoiceBox<String> choiceOrdenar;
    @FXML private ChoiceBox<TransactionType> choiceTipo;
    @FXML private ChoiceBox<String> choiceCategoria;
    @FXML private TextField txtValor;
    @FXML private DatePicker dateData;

    private TransactionService service = new TransactionService();

    @FXML
    public void initialize() {
        loadChoices();
        loadTable();
    }

    private void loadChoices() {
    	
    	ObservableList<String> listaOrdenacao = FXCollections.observableArrayList(
                "Mês", 
                "Tipo"
         );
            
        choiceOrdenar.setItems(listaOrdenacao);
        
     // Preencher tipos no ChoiceBox
        choiceTipo.getItems().setAll(TransactionType.values());

      // atualizar categorias de acordo com o tipo
        choiceTipo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, novoTipo) -> {
            atualizarCategorias(novoTipo);
        });
    }

    private void loadTable() {
        tableTransacoes.getItems().setAll(service.getAll());
    }

    @FXML
    private void handleAdd() {

        try {
            // validação dos campos do formulário

            if (choiceTipo.getValue() == null) {
                showAlert("Selecione um tipo.");
                return;
            }

            if (choiceCategoria.getValue() == null) {
                showAlert("Selecione uma categoria.");
                return;
            }

            if (txtValor.getText().isBlank()) {
                showAlert("Informe o valor.");
                return;
            }

            if (dateData.getValue() == null) {
                showAlert("Selecione uma data.");
                return;
            }

            double valor;
            try {
                valor = Double.parseDouble(txtValor.getText());
            } catch (NumberFormatException e) {
                showAlert("Valor inválido! Digite apenas números.");
                return;
            }

            // criar uma nova transação
  
            Transaction nova = new Transaction(choiceTipo.getValue(), choiceCategoria.getValue(), valor, dateData.getValue());

            // salvar pelo service

            service.addTransaction(nova);

            // atualizar tabela

            tableTransacoes.getItems().setAll(service.getAll());

            // limpar campos

            choiceTipo.setValue(null);
            choiceCategoria.setValue(null);
            txtValor.clear();
            dateData.setValue(null);

            showAlert("Transação adicionada com sucesso!", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro ao adicionar transação.");
        }
    }
    

    @FXML
    private void handleEdit() {
        // implementar
    }

    @FXML
    private void handleRemove() {
        // implementar
    }

    @FXML
    private void goToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ufrn/finetrack/view/HomeView.fxml")
            );

            Scene scene = new Scene(loader.load(), 1280, 720);
            Stage stage = (Stage) tableTransacoes.getScene().getWindow();

            scene.getStylesheets().add(
                getClass().getResource("/ufrn/finetrack/view/style.css").toExternalForm()
            );

            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToReports() {
        // navegação
    }
    
    
    private void showAlert(String msg) {
        showAlert(msg, Alert.AlertType.WARNING);
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    private void atualizarCategorias(TransactionType tipo) {
        if (tipo == null) return;

        switch (tipo) {
            case DESPESA:
                choiceCategoria.getItems().setAll(
                    Arrays.stream(ExpenseCategory.values())
                          .map(Enum::name)
                          .toList()
                );
                break;

            case RECEITA:
                choiceCategoria.getItems().setAll(
                    Arrays.stream(IncomeCategory.values())
                          .map(Enum::name)
                          .toList()
                );
                break;
        }
    }
}


package ufrn.finetrack.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
            
        // Define os itens na ChoiceBox
        choiceOrdenar.setItems(listaOrdenacao);
        
        choiceTipo.getItems().setAll(TransactionType.values());
        choiceCategoria.getItems().setAll("Alimentos", "Transporte", "Contas fixas");
    }

    private void loadTable() {
        tableTransacoes.getItems().setAll(service.getAll());
    }

    @FXML
    private void handleAdd() {
        // implementar
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
        // navegação
    }

    @FXML
    private void goToReports() {
        // navegação
    }
}


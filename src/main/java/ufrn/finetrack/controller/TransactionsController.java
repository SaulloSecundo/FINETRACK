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
    
    private Transaction transacaoEdicaoOuRemocao = null;

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
        
     // quando selecionar um item da table, preenche os campos e marca para edição ou remoção
        tableTransacoes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, novo) -> {
            if (novo != null) {
                transacaoEdicaoOuRemocao = novo;

                choiceTipo.setValue(novo.getTipo());
                choiceCategoria.setValue(novo.getCategoria());
                txtValor.setText(String.valueOf(novo.getValor()));
                dateData.setValue(novo.getData());
            }
        });
    }

    @FXML
    private void handleAdd() {
    	
    	if (transacaoEdicaoOuRemocao != null) {
            showAlert("Você selecionou uma transação da tabela. Use os botões EDITAR ou REMOVER para continuar a operação");
            return;
        }

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

        if (transacaoEdicaoOuRemocao == null) {
            showAlert("Selecione uma transação na tabela para editar.");
            return;
        }

        try {
            // Valida o formulário
            if (choiceTipo.getValue() == null ||
                choiceCategoria.getValue() == null ||
                txtValor.getText().isBlank() ||
                dateData.getValue() == null) {

                showAlert("Preencha todos os campos antes de editar.");
                return;
            }

            double novoValor;
            try {
                novoValor = Double.parseDouble(txtValor.getText());
            } catch (NumberFormatException e) {
                showAlert("Valor inválido. Use apenas números.");
                return;
            }

            // Atualiza os campos da transação mantida no json
            transacaoEdicaoOuRemocao.setTipo(choiceTipo.getValue());
            transacaoEdicaoOuRemocao.setCategoria(choiceCategoria.getValue());
            transacaoEdicaoOuRemocao.setValor(novoValor);
            transacaoEdicaoOuRemocao.setData(dateData.getValue());

            // Salva via service (vai atualizar e persistir)
            service.updateTransaction(transacaoEdicaoOuRemocao);

            // Recarrega a tabela
            tableTransacoes.getItems().setAll(service.getAll());
            
         // limpar campos

            limparCampos();

            showAlert("Transação atualizada com sucesso!", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro ao editar transação.");
        }
    }


    @FXML
    private void handleRemove() {

        if (transacaoEdicaoOuRemocao == null) {
            showAlert("Selecione uma transação na tabela para remover.");
            return;
        }

        if (!confirmar("Deseja realmente remover esta transação?")) {
            return;
        }

        try {
            service.removeTransaction(transacaoEdicaoOuRemocao.getId());

            tableTransacoes.getItems().setAll(service.getAll());

            limparCampos();

            showAlert("Transação removida com sucesso!", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro ao remover transação.");
        }
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
    
    private boolean confirmar(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText(null);
        alert.setContentText(msg);

        return alert.showAndWait().filter(btn -> btn == ButtonType.OK).isPresent();
    }
    
    private void limparCampos() {
        choiceTipo.setValue(null);
        choiceCategoria.setValue(null);
        txtValor.clear();
        dateData.setValue(null);

        transacaoEdicaoOuRemocao = null; //volta ao modo "Adicionar"
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


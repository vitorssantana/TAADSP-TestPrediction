package view;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import controller.DesenvolvedorController;
import controller.DesenvolvedorRequisitoController;
import controller.DesenvolvedorRequisitoSprintController;
import controller.RequisitoSprintController;
import controller.SprintController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Desenvolvedor;
import model.DesenvolvedorRequisitoSprint;
import model.RequisitoSprint;
import model.Sprint;
import utils.AlertController;
import model.Desenvolvedor;

public class DesenvolvedorFX implements Initializable {

	private boolean isCadastrarNovoDesenvolvedor = true;

	@FXML
	private Button btnSalvar;
	@FXML
	private TextField nome;
	@FXML
	private ComboBox<String> selectNivel;
	@FXML
	private TableColumn<Desenvolvedor, Integer> listaId;
	@FXML
	private TableColumn<Desenvolvedor, String> listaNome;
	@FXML
	private TableColumn<Desenvolvedor, Integer> listaNivel;
	@FXML
	private TableView<Desenvolvedor> listaDesenvolvedores;
	private DesenvolvedorController controller;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			controller = new DesenvolvedorController();
			carregarListaDesenvolvedors();
			carregarListaPrioridade();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void limparCamposTela() {
		nome.clear();
		selectNivel.getSelectionModel().clearSelection();
	}

	@FXML
	public void editarDesenvolvedor() {
		if (listaDesenvolvedores.getSelectionModel().getSelectedItem() == null)
			AlertController.alertUsingWarningDialog("Selecione um desenvolvedor da lista");
		else {
			nome.clear();
			nome.setText(listaDesenvolvedores.getSelectionModel().getSelectedItem().getNome());

			for (int i = 0; i < selectNivel.getItems().size(); i++) {
				if (selectNivel.getItems().get(i)
						.equals(listaDesenvolvedores.getSelectionModel().getSelectedItem().getNivel()))
					selectNivel.getSelectionModel().select((selectNivel.getItems().get(i)));
			}

			isCadastrarNovoDesenvolvedor = false;
		}
	}

	@FXML
	public void confirmarDesenvolvedor(Event e) throws IOException {

		Desenvolvedor desenvolvedor = new Desenvolvedor();
		desenvolvedor.setNome(nome.getText());
		desenvolvedor.setNivel(selectNivel.getSelectionModel().getSelectedItem().toString());

		if (isCadastrarNovoDesenvolvedor == true) {
			controller.cadastrarNovoDesenvovedor(desenvolvedor);
			carregarListaDesenvolvedors();

			AlertController.alertUsingInformationDialog("Cadastro feito com sucesso!");
		} else {
			desenvolvedor.setId(listaDesenvolvedores.getSelectionModel().getSelectedItem().getId());
			controller.editarDesenvolvedor(desenvolvedor);
			carregarListaDesenvolvedors();
			AlertController.alertUsingInformationDialog("Alteração feita com sucesso!");
		}

		limparCamposTela();
	}

	private void carregarListaDesenvolvedors() {
		List<Desenvolvedor> listaDesenvolvedores = controller.enviarListaDesenvolvedor();
		setTableContent(listaDesenvolvedores);
		if (listaDesenvolvedores.size() > 0) {
			listaId.setCellValueFactory(new PropertyValueFactory<Desenvolvedor, Integer>("id"));
			listaNome.setCellValueFactory(new PropertyValueFactory<Desenvolvedor, String>("nome"));
			listaNivel.setCellValueFactory(new PropertyValueFactory<Desenvolvedor, Integer>("nivel"));
		}
	}

	@FXML
	private void removerDesenvolvedor() throws IOException {
		if (listaDesenvolvedores.getSelectionModel().getSelectedItem() == null)
			AlertController.alertUsingWarningDialog("Selecione um desenvolvedor da lista");
		else {
			// verificar se existe sprint aberta
			// se existir sprint aberta, ver se o desenvolvedor selecionado esta sendo usado
			List<Sprint> listaSprint = new SprintController().enviarListaSprint();
			for (int i = 0; i < listaSprint.size(); i++) {
				if(listaSprint.get(i).getStatus().equals("Em Andamento")) {
					List<RequisitoSprint> requisitoSprint = new RequisitoSprintController().retornarListaRequisitoSprint();
					for(int j =0; j<requisitoSprint.size(); j ++) {
						if(requisitoSprint.get(j).getIdSprint() == listaSprint.get(i).getId()) {
							List<DesenvolvedorRequisitoSprint> listaDevReqSprint = new DesenvolvedorRequisitoSprintController().retornarListaDesenvolvedorRequisitoSprint();
							for(int k=0; k<listaDevReqSprint.size(); k++) {
								if(listaDevReqSprint.get(k).getIdRequisitoSprint() == requisitoSprint.get(j).getId() && listaDevReqSprint.get(k).getIdDesenvolvedor() == listaDesenvolvedores.getSelectionModel().getSelectedItem().getId()) {
									AlertController.alertUsingWarningDialog("O desenvolvedor atualmente esta sendo utilizado na sprint atual");
									return;
								}
							}
						}
					}
				}  if(i == listaSprint.size()-1) {
					controller.removerDesenvolvedor(listaDesenvolvedores.getSelectionModel().getSelectedItem());
					AlertController.alertUsingSuccessDialog("Desenvolvedor deletado com sucesso!");
					carregarListaDesenvolvedors();
					return;
				}
			}
		}

	}

	private void carregarListaPrioridade() {
		selectNivel.getItems().addAll("Junior", "Pleno", "Senior");
	}

	private void setTableContent(List<Desenvolvedor> listaDesenvolvedor) {
		listaDesenvolvedores.getItems().setAll(listaDesenvolvedor);
	}
}

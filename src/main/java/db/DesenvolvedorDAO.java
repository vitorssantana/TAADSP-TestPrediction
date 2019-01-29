package db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.Desenvolvedor;

public class DesenvolvedorDAO {
	private XlsDAO xlsDAO;
	private XSSFWorkbook workbook;
	private XSSFSheet abaDesenvolvedor;

	public DesenvolvedorDAO() throws IOException {
		xlsDAO = new XlsDAO();
		workbook = xlsDAO.getWorkbook();
		abaDesenvolvedor = workbook.getSheet("Desenvolvedor");
	}

	public List<Desenvolvedor> retornarListaDesenvolvedores() {

		List<Desenvolvedor> listaDesenvolvedores = new ArrayList<Desenvolvedor>();
		Iterator<Row> iterator = abaDesenvolvedor.iterator();

		Row nextRow;
		if (abaDesenvolvedor.getRow(1) != null) {
			while (iterator.hasNext()) {
				nextRow = iterator.next();
				if (nextRow.getRowNum() == 0)
					nextRow = iterator.next();

				Iterator<Cell> cellIterator = nextRow.cellIterator();
				cellIterator = nextRow.cellIterator();
				Desenvolvedor desenvolvedor = new Desenvolvedor();

				while (cellIterator.hasNext()) {
					Cell nextCell = cellIterator.next();
					int columnIndex = nextCell.getColumnIndex();

					switch (columnIndex) {

					case 0:
						desenvolvedor.setId((int) nextCell.getNumericCellValue());
						break;

					case 1:
						desenvolvedor.setNome(nextCell.getStringCellValue());
						break;

					case 2:
						desenvolvedor.setNivel(nextCell.getStringCellValue());
						break;

					}

				}
				listaDesenvolvedores.add(desenvolvedor);
			}
		}

		return listaDesenvolvedores;
	}

	public void addNewDesenvolvedor(Desenvolvedor desenvolvedor) {
		Iterator<Row> iterator = abaDesenvolvedor.iterator();
		int count = 0;

		while (abaDesenvolvedor.getRow(count) != null) {
			count++;
		}

		Row row = abaDesenvolvedor.createRow(count);

		Cell cell = row.createCell(0);
		cell.setCellValue(count);

		cell = row.createCell(1);
		cell.setCellValue(desenvolvedor.getNome());

		cell = row.createCell(2);
		cell.setCellValue(desenvolvedor.getNivel());

		xlsDAO.writeAndCloseXls();
	}

}

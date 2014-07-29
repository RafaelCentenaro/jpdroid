package br.com.rafael.pedidojpdroid;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;
import br.com.rafael.jpdroid.core.Jpdroid;
import br.com.rafael.jpdroid.util.JpdroidCsvFile;
import br.com.rafael.jpdroid.util.JpdroidJsonFile;
import br.com.rafael.jpdroid.util.JpdroidXmlFile;
import br.com.rafael.pedidojpdroid.entity.Pedido;
import br.com.rafael.pedidojpdroid.entity.Pessoa;
import br.com.rafael.pedidojpdroid.entity.Produto;

public class ExportacoesActivity extends Activity {

	private static final int EXPORT_PESSOA = 3;
	private static final int EXPORT_PRODUTO = 2;
	private static final int EXPORT_PEDIDO = 1;
	private ImageButton btExportPedido;
	private ImageButton btExportProduto;
	private ImageButton btExportPessoa;
	private int buttonClick;
	private DatePicker dataInicial;
	private DatePicker dataFim;
	private CheckBox chkPeriodo;

	Jpdroid jpdroid = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exportacoes);

		jpdroid = Jpdroid.getInstance();

		btExportPedido = (ImageButton) findViewById(R.id.btExportPedido);
		registerForContextMenu(btExportPedido);

		btExportProduto = (ImageButton) findViewById(R.id.btExportProduto);
		registerForContextMenu(btExportProduto);

		btExportPessoa = (ImageButton) findViewById(R.id.btExportPessoa);

		dataInicial = (DatePicker) findViewById(R.id.datePicker1);
		dataFim = (DatePicker) findViewById(R.id.datePicker2);

		chkPeriodo = (CheckBox) findViewById(R.id.checkBox1);

		registerForContextMenu(btExportPessoa);
	}

	public static java.util.Date getDateFromDatePicket(DatePicker datePicker) {
		int day = datePicker.getDayOfMonth();
		int month = datePicker.getMonth();
		int year = datePicker.getYear();

		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);

		return calendar.getTime();
	}

	@SuppressLint("SimpleDateFormat")
	public static String getDataFormat(DatePicker datePicker) {
		return new SimpleDateFormat("yyyy-MM-dd").format(getDateFromDatePicket(datePicker));
	}

	public void onClickExportPedido(View v) {
		buttonClick = EXPORT_PEDIDO;
		openContextMenu(v);
	}

	public void onClickExportProduto(View v) {
		buttonClick = EXPORT_PRODUTO;
		openContextMenu(v);
	}

	public void onClickExportPessoa(View v) {
		buttonClick = EXPORT_PESSOA;
		openContextMenu(v);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_exportacao, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.exportCSV:
				exportCSV();
				break;
			case R.id.exportJSON:
				exportJSON();
				break;
			case R.id.exportXML:
				exportXML();
				break;
			default:
				return super.onContextItemSelected(item);
		}
		return super.onContextItemSelected(item);
	}

	private void exportCSV() {
		switch (buttonClick) {
			case EXPORT_PEDIDO:
				if (chkPeriodo.isChecked()) {
					JpdroidCsvFile.export(
					    jpdroid.retrieve(Pedido.class, " date(data) BETWEEN '" + getDataFormat(dataInicial) + "' AND '"
					        + getDataFormat(dataFim) + "' ", true), "PedidoExport.csv");
				} else {
					JpdroidCsvFile.export(jpdroid.retrieve(Pedido.class, true), "PedidoExport.csv");
				}
				break;
			case EXPORT_PRODUTO:
				JpdroidCsvFile.export(jpdroid.retrieve(Produto.class, true), "ProdutoExport.csv");
				break;
			case EXPORT_PESSOA:
				JpdroidCsvFile.export(jpdroid.retrieve(Pessoa.class, true), "PessoaExport.csv");
				break;
			default:
		}
		Toast.makeText(getBaseContext(), "Exportação realizada!", Toast.LENGTH_SHORT).show();
	}

	private void exportJSON() {
		switch (buttonClick) {
			case EXPORT_PEDIDO:
				if (chkPeriodo.isChecked()) {
					JpdroidJsonFile.export(
					    jpdroid.retrieve(Pedido.class, " date(data) BETWEEN '" + getDataFormat(dataInicial) + "' AND '"
					        + getDataFormat(dataFim) + "' ", true), "PedidoExport.json");
				} else {
					JpdroidJsonFile.export(jpdroid.retrieve(Pedido.class, true), "PedidoExport.json");
				}
				break;
			case EXPORT_PRODUTO:
				JpdroidJsonFile.export(jpdroid.retrieve(Produto.class, true), "ProdutoExport.json");
				break;
			case EXPORT_PESSOA:
				JpdroidJsonFile.export(jpdroid.retrieve(Pessoa.class, true), "PessoaExport.json");
				break;
			default:
		}
		Toast.makeText(getBaseContext(), "Exportação realizada!", Toast.LENGTH_SHORT).show();
	}

	private void exportXML() {
		switch (buttonClick) {
			case EXPORT_PEDIDO:
				if (chkPeriodo.isChecked()) {
					JpdroidXmlFile.export(
					    jpdroid.retrieve(Pedido.class, " date(data) BETWEEN '" + getDataFormat(dataInicial) + "' AND '"
					        + getDataFormat(dataFim) + "' ", true), "PedidoExport.xml");
				} else {
					JpdroidXmlFile.export(jpdroid.retrieve(Pedido.class, true), "PedidoExport.xml");
				}
				break;
			case EXPORT_PRODUTO:
				JpdroidXmlFile.export(jpdroid.retrieve(Produto.class, true), "ProdutoExport.xml");
				break;
			case EXPORT_PESSOA:
				JpdroidXmlFile.export(jpdroid.retrieve(Pessoa.class, true), "PessoaExport.xml");
				break;
			default:
		}
		Toast.makeText(getBaseContext(), "Exportação realizada!", Toast.LENGTH_SHORT).show();
	}

}

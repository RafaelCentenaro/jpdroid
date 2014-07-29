package br.com.rafael.pedidojpdroid;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import br.com.rafael.jpdroid.core.Jpdroid;
import br.com.rafael.jpdroid.util.JpdroidConverter;
import br.com.rafael.pedidojpdroid.entity.ItensPedido;
import br.com.rafael.pedidojpdroid.entity.Pedido;
import br.com.rafael.pedidojpdroid.entity.Pessoa;

public class PedidoActivity extends TabActivity {

	private static final int ADD_PRODUTO = 0;
	private Pedido pedido = new Pedido();
	private List<ItensPedido> itensPedido = new ArrayList<ItensPedido>();

	private EditText etIdCliente;
	private EditText etNomeCliente;
	private EditText etData;
	private Spinner spEnderecoEntrega;
	private ListView lvItensPedido;
	private TextView tvTotalPedido;
	private long _id;

	private Jpdroid jpdroid;

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedido);

		jpdroid = Jpdroid.getInstance();

		etIdCliente = (EditText) findViewById(R.id.etIdClientePedido);
		etNomeCliente = (EditText) findViewById(R.id.etNomeClientePedido);
		spEnderecoEntrega = (Spinner) findViewById(R.id.spEnderecoEntrega);
		tvTotalPedido = (TextView) findViewById(R.id.tvTotalPedido);

		lvItensPedido = (ListView) findViewById(R.id.lvItensPedido);

		etData = (EditText) findViewById(R.id.etData);

		etData.setText(new SimpleDateFormat("dd/MM/yyyy").format(jpdroid.getDate()));

		// TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);

		TabSpec descritor = getTabHost().newTabSpec("tag1");
		descritor.setContent(R.id.pedido);
		descritor.setIndicator("Pedido", getResources().getDrawable(R.drawable.pedido));
		getTabHost().addTab(descritor);

		descritor = getTabHost().newTabSpec("tag2");
		descritor.setContent(R.id.itensPedido);
		descritor.setIndicator("Produtos", getResources().getDrawable(R.drawable.itenspedido));
		getTabHost().addTab(descritor);

		getTabHost().setCurrentTab(0);

		Intent i = getIntent();
		_id = i.getLongExtra("_id", 0);
		if (_id > 0) {
			pedido = (Pedido) jpdroid.retrieve(Pedido.class, "_id = " + _id, true).get(0);
			itensPedido = pedido.getItensPedido();
			etIdCliente.setText(String.valueOf(pedido.getIdCliente()));
			etNomeCliente.setText(pedido.getCliente().getNome());
			pedido.setCliente(null);
			fillItensPedido();
			fillEnderecos(pedido.getIdCliente());
			SelectSpinnerItemByValue(spEnderecoEntrega, pedido.getIdEnderecoEntrega());
		} else {
			pedido = new Pedido();
		}
		registerForContextMenu(lvItensPedido);

		// Oculta teclado ao criar a Activity
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

	}

	private void SelectSpinnerItemByValue(Spinner spEndereco, long idEndereco) {
		SimpleCursorAdapter adapter = (SimpleCursorAdapter) spEndereco.getAdapter();
		for (int position = 0; position < adapter.getCount(); position++) {
			if (adapter.getItemId(position) == idEndereco) {
				spEndereco.setSelection(position);
				return;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pedido, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.addItens) {
			Intent i = new Intent(this, ItensPedidoActivity.class);
			i.putExtra("_id", 0);
			startActivityForResult(i, ADD_PRODUTO);
			getTabHost().setCurrentTab(1);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void PesquisaClienteDialog() {
		final Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.activity_pesquisa_dialog);

		// define o título do Dialog
		dialog.setTitle("Busca de cliente:");

		// instancia os objetos que estão no layout customdialog.xml
		final ImageButton pesquisa = (ImageButton) dialog.findViewById(R.id.btPesquisaDialog);
		final ImageButton limpar = (ImageButton) dialog.findViewById(R.id.btLimparPesquisa);
		final EditText editPesquisaCliente = (EditText) dialog.findViewById(R.id.etPesquisa);
		final ListView lvPesquisaCliente = (ListView) dialog.findViewById(R.id.lvListagemPesquisa);
		lvPesquisaCliente.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {

				Cursor cursorPessoa = (Cursor) lvPesquisaCliente.getItemAtPosition(position);

				fillEnderecos(id);

				etIdCliente.setText(String.valueOf(id));
				etNomeCliente.setText(cursorPessoa.getString(cursorPessoa.getColumnIndex("nome")));
				spEnderecoEntrega.requestFocus();
				dialog.dismiss();
			}

			private void fillEnderecos(long id) {

				// Cursor cursorEnderecos = jpdroid.createQuery(Endereco.class, "idPessoa = "+id);
				String[] columns = new String[] { "endereco" };

				int[] to = new int[] { android.R.id.text1 };

				// Cursor matrixCursor = jpdroid.createQuery(Endereco.class, "idPessoa = "+id);
				Cursor matrixCursor = jpdroid.rawQuery(
				    "SELECT _id, rua ||', '|| numero ||', '|| bairro as endereco from endereco where idpessoa = " + id, null);

				SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(spEnderecoEntrega.getContext(),
				    android.R.layout.simple_list_item_1, matrixCursor, columns, to, 0);
				spEnderecoEntrega.setAdapter(dataAdapter);

			}

		});
		pesquisa.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				String filtro = editPesquisaCliente.getText().toString();
				String[] columns = new String[] { "_id", "nome" };

				int[] to = new int[] { R.id.tvIdPessoaListagem, R.id.tvNomePessoaListagem };

				String where = "";
				if (filtro.trim().matches("^[0-9]*$") && filtro.trim().length() > 0) {
					where = "_id = " + filtro;
				} else {
					where = "nome like '%" + filtro + "%'";
				}
				Cursor matrixCursor = jpdroid.createQuery(Pessoa.class, where);

				SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(v.getContext(), R.layout.activity_lista_pessoa,
				    matrixCursor, columns, to, 0);

				lvPesquisaCliente.setAdapter(dataAdapter);

			}

		});

		limpar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				editPesquisaCliente.setText("");
			}
		});

		// exibe na tela o dialog
		dialog.show();

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// menu.setHeaderTitle(getString(R.string.));
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_produto, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.editarProduto:
				Intent it = new Intent(this, ItensPedidoActivity.class);

				Bundle bundle = new Bundle();
				bundle.putSerializable("itensPedido", (Serializable) itensPedido.get(info.position));

				it.putExtras(bundle);
				it.putExtra("posicao", info.position);

				startActivityForResult(it, ADD_PRODUTO);

				break;
			case R.id.excluirProduto:
				DeleteProduto(info.position);
				break;
			default:
				return super.onContextItemSelected(item);
		}
		return super.onContextItemSelected(item);
	}

	private void DeleteProduto(int position) {
		ItensPedido del = itensPedido.get(position);
		if (del.get_id() > 0) {
			jpdroid.delete(del);
		}
		itensPedido.remove(position);
		fillItensPedido();

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADD_PRODUTO) {
			if (resultCode == Activity.RESULT_OK && data != null) {
				ItensPedido novo = (ItensPedido) data.getExtras().getSerializable("itensPedido");
				if (novo.get_id() == 0) {
					itensPedido.add(novo);
				} else {
					itensPedido.set(data.getIntExtra("posicao", 0), novo);
				}
				fillItensPedido();
			}
		}
	}

	private void fillItensPedido() {
		String[] columns = new String[] { "_id", "nomeProduto", "qtdProduto", "valorUnitario" };

		int[] to = new int[] { R.id.tvIdItemPedido, R.id.tvProdudtoItens, R.id.tvQtdItens, R.id.tvValorItens };

		MatrixCursor matrixCursor = JpdroidConverter.toMatrixCursor(itensPedido, false);

		SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(this, R.layout.activity_lista_itens_pedido, matrixCursor,
		    columns, to, 0);

		lvItensPedido.setAdapter(dataAdapter);

		tvTotalPedido.setText("Total: R$ " + String.valueOf(getValorTotal()));

	}

	private void fillEnderecos(long id) {

		// Cursor cursorEnderecos = jpdroid.createQuery(Endereco.class, "idPessoa = "+id);
		String[] columns = new String[] { "endereco" };

		int[] to = new int[] { android.R.id.text1 };

		// Cursor matrixCursor = jpdroid.createQuery(Endereco.class, "idPessoa = "+id);
		Cursor matrixCursor = jpdroid.rawQuery(
		    "SELECT _id, rua ||', '|| numero ||', '|| bairro as endereco from endereco where idpessoa = " + id, null);

		SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(spEnderecoEntrega.getContext(),
		    android.R.layout.simple_list_item_1, matrixCursor, columns, to, 0);
		spEnderecoEntrega.setAdapter(dataAdapter);

	}

	public void onClickPesquisaClientePedido(View v) {
		PesquisaClienteDialog();
	}

	public void ClickSalvarPedido(View v) {
		if (!validar()) {
			return;
		}
		try {
			pedido.setIdCliente(Long.valueOf(etIdCliente.getText().toString()));
			Cursor crEndereco = (Cursor) spEnderecoEntrega.getSelectedItem();
			if (crEndereco != null) {
				pedido.setIdEnderecoEntrega(crEndereco.getLong(crEndereco.getColumnIndex("_id")));
			}
			pedido.setData(jpdroid.getDate());
			pedido.setItensPedido(itensPedido);

			pedido.setValorTotal(getValorTotal());
			jpdroid.persist(pedido);
			finish();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean validar() {
		Editable text = etIdCliente.getText();
		if (text != null) {
			String strText = text.toString();
			if (!TextUtils.isEmpty(strText)) {
				return true;
			}
		}
		Toast.makeText(getBaseContext(), "Informe um cliente!", Toast.LENGTH_SHORT).show();
		etIdCliente.setFocusable(true);
		etIdCliente.requestFocus();
		PesquisaClienteDialog();
		return false;
	}

	private double getValorTotal() {
		double total = 0;
		for (ItensPedido item : itensPedido) {
			total += item.getQtdProduto() * item.getValorUnitario();
		}
		return total;
	}

}

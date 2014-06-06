package br.com.rafael.pedidojpdroid;

import java.io.Serializable;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import br.com.rafael.jpdroid.core.Jpdroid;
import br.com.rafael.pedidojpdroid.entity.ItensPedido;
import br.com.rafael.pedidojpdroid.entity.Produto;

public class ItensPedidoActivity extends Activity {

	private EditText etIdProduto;
	private EditText etNomeProduto;
	private EditText etQtdItem;
	private EditText etValorItem;
	private ItensPedido itensPedido;
	private int posicao;
	private Jpdroid jpdroid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_itens_pedido);

		jpdroid = Jpdroid.getInstance();

		etIdProduto = (EditText) findViewById(R.id.etIdProdutoPedido);
		etNomeProduto = (EditText) findViewById(R.id.etNomeProdutoPedido);
		etQtdItem = (EditText) findViewById(R.id.etQtdItem);
		etValorItem = (EditText) findViewById(R.id.etValorItem);

		Intent it = getIntent();
		Serializable param = it.getExtras().getSerializable("itensPedido");
		if (param != null) {
			posicao = it.getIntExtra("posicao", 0);
			ItensPedido novo = (ItensPedido) param;
			itensPedido = novo;
			etIdProduto.setText(String.valueOf(itensPedido.getIdProduto()));
			etNomeProduto.setText(getNomeProduto(itensPedido.getIdProduto()));
			etQtdItem.setText(String.valueOf(itensPedido.getQtdProduto()));
			etValorItem.setText(String.valueOf(itensPedido.getValorUnitario()));
		} else {
			itensPedido = new ItensPedido();
		}
	}


	private String getNomeProduto(long idProduto) {
		Produto produto = jpdroid.getObjects(Produto.class,"_id = "+idProduto).get(0);
	  return produto.getNome();
  }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.itens_pedido, menu);
		return true;
	}

	public void onbtPesquisaItem(View v) {
		PesquisaProdutoDialog();
	}

	private void PesquisaProdutoDialog() {
		final Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.activity_pesquisa_dialog);

		// define o título do Dialog
		dialog.setTitle("Busca de Produtos:");

		// instancia os objetos que estão no layout customdialog.xml
		final ImageButton pesquisa = (ImageButton) dialog.findViewById(R.id.btPesquisaDialog);
		final ImageButton limpar = (ImageButton) dialog.findViewById(R.id.btLimparPesquisa);
		final EditText editPesquisaCliente = (EditText) dialog.findViewById(R.id.etPesquisa);
		final ListView lvPesquisaCliente = (ListView) dialog.findViewById(R.id.lvListagemPesquisa);
		lvPesquisaCliente.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {

				Cursor cursorProduto = (Cursor) lvPesquisaCliente.getItemAtPosition(position);
				etIdProduto.setText(String.valueOf(id));
				etNomeProduto.setText(cursorProduto.getString(cursorProduto.getColumnIndex("nome")));
				etQtdItem.setText("1");
				etValorItem.setText(String.valueOf(cursorProduto.getDouble(cursorProduto.getColumnIndex("preco"))));
				itensPedido.setIdProduto(id);
				itensPedido.setNomeProduto(etNomeProduto.getText().toString());
				etQtdItem.requestFocus();
				dialog.dismiss();
			}

		});
		pesquisa.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				String filtro = editPesquisaCliente.getText().toString();
				String[] columns = new String[] { "_id", "nome" };

				int[] to = new int[] { R.id.tvIdProdutoListagem, R.id.tvNomeProdutoListagem };

				String where = "";
				if (filtro.trim().matches("^[0-9]*$") && filtro.trim().length() > 0) {
					where = "_id = " + filtro;
				} else {
					where = "nome like '%" + filtro + "%'";
				}
				Cursor matrixCursor = jpdroid.createQuery(Produto.class, where);

				SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(v.getContext(), R.layout.activity_lista_produto,
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
	private boolean validar() {
		Editable text = etIdProduto.getText();
	   if (text != null) {
	    String strText = text.toString();
	    if (!TextUtils.isEmpty(strText)) {
	     return true;
	    }
	   }
	 	Toast.makeText(getBaseContext(), "Informe um produto!", Toast.LENGTH_SHORT).show();
	 	etIdProduto.setFocusable(true);
	 	etIdProduto.requestFocus();
	 	PesquisaProdutoDialog();
	  return false;
  }
	public void btnSalvarItensPedidoClicked(final View v) {

  	if(!validar()){
  		return;
  	}
		itensPedido.setIdProduto(Long.valueOf(etIdProduto.getText().toString()));
		itensPedido.setQtdProduto(Double.valueOf(etQtdItem.getText().toString()));
		itensPedido.setValorUnitario(Double.valueOf(etValorItem.getText().toString()));
		

		Intent it = new Intent();

		Bundle bundle = new Bundle();
		bundle.putSerializable("itensPedido", (Serializable) itensPedido);

		it.putExtras(bundle);
		it.putExtra("posicao", posicao);
		setResult(RESULT_OK, it);
		finish();
	}

}

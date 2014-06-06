package br.com.rafael.pedidojpdroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import br.com.rafael.jpdroid.core.Jpdroid;
import br.com.rafael.pedidojpdroid.entity.Pedido;

public class ListagemPedidoActivity extends Activity {

	ListView lvListaPedidos;
	EditText etPesquisa;
	Jpdroid jpdroid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listagem_pedido);

		jpdroid = Jpdroid.getInstance();

		lvListaPedidos = (ListView) findViewById(R.id.lvListagemPedido);
		etPesquisa = (EditText) findViewById(R.id.etPesquisaPedido);

		registerForContextMenu(lvListaPedidos);
		
		fillPedido(etPesquisa.getText().toString());
		//Oculta teclado ao criar a Activity
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); 

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// menu.setHeaderTitle(getString(R.string.));
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_pedido, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.excluirPedido:
				deletePedido(info.position);
				break;
			case R.id.editarPedido:
				Intent i = new Intent(this, PedidoActivity.class);
				Cursor cursor = (Cursor) lvListaPedidos.getItemAtPosition(info.position);
				long id = cursor.getLong(cursor.getColumnIndex("_id"));
				i.putExtra("_id", id);
				startActivity(i);
				break;
			default:
				return super.onContextItemSelected(item);
		}
		return super.onContextItemSelected(item);
	}
	private void deletePedido(final int posicao) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Confirma a exclusão?");
		builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(jpdroid.delete(Pedido.class, (Cursor) lvListaPedidos.getItemAtPosition(posicao)) <= 0){
					 Toast.makeText(getBaseContext(), "O pedido não pode ser excluído!", Toast.LENGTH_SHORT).show();
				}
				fillPedido(etPesquisa.getText().toString());

				dialog.dismiss();
			}
		});
		builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.show();

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.listagem_pedido, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.addPedido) {
			Intent i = new Intent(this, PedidoActivity.class);
			i.putExtra("_id", 0);
			startActivity(i);

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void onClickPesquisaPedido(View v) {
		fillPedido(etPesquisa.getText().toString());
	}

	public void onClickLimparPesquisa(View v) {
		etPesquisa.setText("");
	}

	private void fillPedido(String filtro) {
		String[] columns = new String[] { "_id", "nomeCliente", "total", "dataGravacao" };

		int[] to = new int[] { R.id.tvIdPedidoListagem, R.id.tvNomeCliente, R.id.tvValorTotal, R.id.tvdata };

		String where = "0 = 0";
		if (filtro.trim().length() > 0) {
			if (filtro.trim().matches("^[0-9]*$")) {
				where = "PEDIDO._id = " + filtro;
			} else {
				where = "PESSOA.NOME like '%" + filtro + "%'";
			}
		}
		String sql = "SELECT PEDIDO._ID as _id, PESSOA.NOME AS nomeCliente, 'R$ '|| valorTotal as total,  strftime('%d/%m/%Y',data)  as dataGravacao  FROM PEDIDO INNER JOIN PESSOA ON (PEDIDO.IDCLIENTE = PESSOA._ID) WHERE "
		    + where;
		Cursor matrixCursor = jpdroid.rawQuery(sql, null);

		SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(this, R.layout.activity_lista_pedido, matrixCursor,
		    columns, to, 0);

		lvListaPedidos.setAdapter(dataAdapter);

	}

	public void onbtLimparPesquisaPessoa(View v) {
		etPesquisa.setText("");
	}

}

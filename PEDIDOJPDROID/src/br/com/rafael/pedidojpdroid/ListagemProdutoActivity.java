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
import br.com.rafael.pedidojpdroid.entity.Produto;

public class ListagemProdutoActivity extends Activity {

	ListView lvListaProduto;
	EditText etPesquisa;
	Jpdroid jpdroid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listagem_produto);

		jpdroid = Jpdroid.getInstance();

		lvListaProduto = (ListView) findViewById(R.id.lvListagemProduto);
		etPesquisa = (EditText) findViewById(R.id.etPesquisaProduto);

		registerForContextMenu(lvListaProduto);
		
		fillProduto(etPesquisa.getText().toString());
		//Oculta teclado ao criar a Activity
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); 
	}

	public void onbtPesquisaProduto(View v) {
		fillProduto(etPesquisa.getText().toString());
	}

	public void onbtLimparPesquisaProduto(View v) {
		etPesquisa.setText("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.listagem_produto, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.addProduto) {
			Intent i = new Intent(this, ProdutoActivity.class);
			i.putExtra("_id", 0);
			startActivity(i);

			return true;
		}

		return super.onOptionsItemSelected(item);
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
			case R.id.excluirProduto:
				deleteProduto(info.position);
				break;
			case R.id.editarProduto:
				Intent i = new Intent(this, ProdutoActivity.class);
				Cursor cursor = (Cursor) lvListaProduto.getItemAtPosition(info.position);
				long id = cursor.getLong(cursor.getColumnIndex("_id"));
				i.putExtra("_id", id);
				startActivity(i);
				break;
			default:
				return super.onContextItemSelected(item);
		}
		return super.onContextItemSelected(item);
	}

	private void fillProduto(String filtro) {
		String[] columns = new String[] { "_id", "nome" };

		int[] to = new int[] { R.id.tvIdProdutoListagem, R.id.tvNomeProdutoListagem };

		String where = "";
		if (filtro.trim().matches("^[0-9]*$") && filtro.trim().length() > 0) {
			where = "_id = " + filtro;
		} else {
			where = "nome like '%" + filtro + "%'";
		}
		Cursor matrixCursor = jpdroid.createQuery(Produto.class, where);

		SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(this, R.layout.activity_lista_produto, matrixCursor,
		    columns, to, 0);

		lvListaProduto.setAdapter(dataAdapter);

	}

	private void deleteProduto(final int posicao) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Confirma a exclusão?");
		builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(jpdroid.delete(Produto.class, (Cursor) lvListaProduto.getItemAtPosition(posicao)) <= 0){
					 Toast.makeText(getBaseContext(), "O produto não pode ser excluído!", Toast.LENGTH_SHORT).show();
				}
				fillProduto(etPesquisa.getText().toString());

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

}

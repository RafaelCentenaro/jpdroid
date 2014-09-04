package com.rafael.androidcomjpdroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import br.com.rafael.jpdroid.core.Jpdroid;
import br.com.rafael.jpdroid.enums.ScriptPath;

import com.rafael.androidcomjpdroid.entity.Contato;
import com.rafael.androidcomjpdroid.entity.Pessoa;
import com.rafael.androidcomjpdroid.entity.TipoContato;

public class MainActivity extends Activity {

	private static final int PESSOA = 1;
	Jpdroid database;
	ListView lvPessoa;
	EditText etPesquisa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		lvPessoa = (ListView) findViewById(R.id.lvPessoa);
		etPesquisa = (EditText) findViewById(R.id.etPesquisa);

		database = Jpdroid.getInstance();
		database.setContext(this);
		database.addEntity(Pessoa.class);
		database.addEntity(Contato.class);
		database.addEntity(TipoContato.class);
		database.open();

		if (database.isCreate()) {
			database.importSqlScript(ScriptPath.Assets, "import.sql");
		}

		adquirirPessoas("");

		registerForContextMenu(lvPessoa);

	}

	public void btnPesquisaOnClick(View v) {
		adquirirPessoas(etPesquisa.getText().toString());
	}

	private void adquirirPessoas(String filtro) {
		String where = "";
		if (filtro.trim().matches("^[0-9]*$") && filtro.trim().length() > 0) {
			where = "_id = " + filtro;
		} else {
			where = "nome like '%" + filtro + "%'";
		}

		Cursor cursor = database.createQuery(Pessoa.class, where, " _id asc ");

		SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, cursor, new String[] {
						"_id", "nome" }, new int[] { android.R.id.text1,
						android.R.id.text2 }, 0);

		lvPessoa.setAdapter(dataAdapter);

	}

	public void btnNovoContatoOnClick(View v) {
		Intent i = new Intent(this, PessoaActivity.class);
		i.putExtra("posicao", 0);
		startActivityForResult(i, PESSOA);

	}

	private void deletePessoa(final int posicao) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Confirma a exclusão?");
		builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (database.delete(Pessoa.class,
						(Cursor) lvPessoa.getItemAtPosition(posicao)) <= 0) {
					Toast.makeText(getBaseContext(),
							"A pessoa não pode ser excluída!",
							Toast.LENGTH_SHORT).show();
				}
				adquirirPessoas(etPesquisa.getText().toString());

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

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PESSOA) {
			if (resultCode == Activity.RESULT_OK && data != null) {
				adquirirPessoas("");
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.pessoa, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.excluirPessoa:
			deletePessoa(info.position);
			break;
		case R.id.editarPessoa:
			Intent i = new Intent(this, PessoaActivity.class);
			Cursor cursor = (Cursor) lvPessoa.getItemAtPosition(info.position);
			long id = cursor.getLong(cursor.getColumnIndex("_id"));
			i.putExtra("_id", id);
			startActivity(i);
			break;
		default:
			return super.onContextItemSelected(item);
		}
		return super.onContextItemSelected(item);
	}
}

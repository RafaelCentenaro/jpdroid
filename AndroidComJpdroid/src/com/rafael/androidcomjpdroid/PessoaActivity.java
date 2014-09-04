package com.rafael.androidcomjpdroid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import br.com.rafael.jpdroid.core.Jpdroid;
import br.com.rafael.jpdroid.exceptions.JpdroidException;
import br.com.rafael.jpdroid.util.JpdroidConverter;

import com.rafael.androidcomjpdroid.entity.Contato;
import com.rafael.androidcomjpdroid.entity.Pessoa;

public class PessoaActivity extends Activity {

	private static final int ADD_CONTATO = 1;
	private Jpdroid database;
	private ListView lvContatos;
	private EditText etNome;
	private Long _id;
	private Pessoa pessoa;
	private List<Contato> contatos = new ArrayList<Contato>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pessoa);

		database = Jpdroid.getInstance();
		lvContatos = (ListView) findViewById(R.id.lvContatos);
		etNome = (EditText) findViewById(R.id.etNome);

		lvContatos.setOnItemClickListener(evento);

		Intent i = getIntent();
		_id = i.getLongExtra("_id", 0);
		if (_id > 0) {
			pessoa = (Pessoa) database.retrieve(Pessoa.class, "_id = " + _id,
					true).get(0);
			contatos = pessoa.getContatos();
			etNome.setText(pessoa.getNome());
			fillContato();
		} else {
			pessoa = new Pessoa();
		}
		registerForContextMenu(lvContatos);
	}

	OnItemClickListener evento = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Contato con = contatos.get(position);
			if (con.getIdTipoContato() == 2 || con.getIdTipoContato() == 3) {
				String uri = "tel:" + con.getContato().trim();
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse(uri));
				startActivity(intent);
			} else if (con.getIdTipoContato() == 1) {
				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL,
						new String[] { con.getContato() });
				email.putExtra(Intent.EXTRA_SUBJECT, "Assunto");
				email.putExtra(Intent.EXTRA_TEXT,
						"Escreva sua mensagem aqui...");
				email.setType("message/rfc822");
				startActivity(Intent.createChooser(email, "Enviar com :"));
			}
		}
	};

	public void btnAddOnClick(View v) {
		Intent i = new Intent(this, ContatoActivity.class);
		i.putExtra("posicao", 0);
		startActivityForResult(i, ADD_CONTATO);

	}

	private void fillContato() {

		MatrixCursor matrixCursor = JpdroidConverter.toMatrixCursor(contatos,
				false);

		SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, matrixCursor,
				new String[] { "nomeTipoContato", "contato" }, new int[] {
						android.R.id.text1, android.R.id.text2 }, 0);

		lvContatos.setAdapter(dataAdapter);
	}

	public void btnCancelarOnClick(View v) {
		Intent it = new Intent();
		setResult(RESULT_CANCELED, it);
		finish();
	}

	private void deleteContato(int position) {
		Contato del = contatos.get(position);
		if (del.get_id() > 0) {
			database.delete(del);
		}
		contatos.remove(position);
		fillContato();

	}

	public void btnSalvarOnClick(View v) {

		try {

			if (etNome.getText() == null
					|| etNome.getText().toString().trim().length() == 0) {
				Toast.makeText(this, "Nome não informado!", Toast.LENGTH_SHORT)
						.show();
				etNome.requestFocus();
				return;
			}
			if (contatos.isEmpty()) {
				Toast.makeText(this, "Favor Cadastrar pelo menos um contato!",
						Toast.LENGTH_SHORT).show();
				return;
			}

			pessoa.setNome(etNome.getText().toString());
			pessoa.setContatos(contatos);

			database.persist(pessoa);

			Intent it = new Intent();
			it.putExtra("_id", _id);
			setResult(RESULT_OK, it);
			finish();

		} catch (JpdroidException e) {

			e.printStackTrace();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADD_CONTATO) {
			if (resultCode == Activity.RESULT_OK && data != null) {
				Contato novo = (Contato) data.getExtras().getSerializable(
						"contato");
				if (novo.get_id() == 0) {
					contatos.add(novo);
				} else {
					contatos.set(data.getIntExtra("posicao", 0), novo);
				}
				fillContato();
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contato, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.excluirContato:
			deleteContato(info.position);
			break;
		case R.id.editarContato:
			Intent it = new Intent(this, ContatoActivity.class);

			Bundle bundle = new Bundle();
			bundle.putSerializable("contato",
					(Serializable) contatos.get(info.position));

			it.putExtras(bundle);
			it.putExtra("posicao", info.position);

			startActivityForResult(it, ADD_CONTATO);

			break;
		default:
			return super.onContextItemSelected(item);
		}
		return super.onContextItemSelected(item);
	}

}

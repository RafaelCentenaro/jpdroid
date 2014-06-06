package br.com.rafael.pedidojpdroid;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import br.com.rafael.jpdroid.core.Jpdroid;
import br.com.rafael.pedidojpdroid.entity.Endereco;

public class EnderecoActivity extends Activity {

	private Endereco endereco = null;
	private EditText etRua;
	private EditText etBairro;
	private EditText etNumero;

	private Spinner spCidade;
	private CheckBox chkPrincipal;
	private Jpdroid jpdroid;
	private int posicao = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_endereco);

		jpdroid = Jpdroid.getInstance();

		etRua = (EditText) findViewById(R.id.etRua);
		etBairro = (EditText) findViewById(R.id.etBairro);
		etNumero = (EditText) findViewById(R.id.etNumero);
		spCidade = (Spinner) findViewById(R.id.spCidade);
		chkPrincipal = (CheckBox) findViewById(R.id.chkPrincipal);

		fillCidade();

		Intent it = getIntent();
		Serializable param = it.getExtras().getSerializable("endereco");
		if (param != null) {
			posicao = it.getIntExtra("posicao", 0);
			Endereco novo = (Endereco) param;
			endereco = novo;
			etRua.setText(endereco.getRua());
			etBairro.setText(endereco.getBairro());
			etNumero.setText(String.valueOf(endereco.getNumero()));
			chkPrincipal.setChecked(endereco.isPrincipal());
			SelectSpinnerItemByValue(spCidade, endereco.getId_Cidade());

		} else {
			endereco = new Endereco();
		}

	}

	private void SelectSpinnerItemByValue(Spinner spCidade2, long id_Cidade) {
		SimpleCursorAdapter adapter = (SimpleCursorAdapter) spCidade2.getAdapter();
		for (int position = 0; position < adapter.getCount(); position++) {
			if (adapter.getItemId(position) == id_Cidade) {
				spCidade2.setSelection(position);
				return;
			}
		}

	}

	private void fillCidade() {
		String[] columns = new String[] { "_id", "nome", "sigla" };

		int[] to = new int[] { R.id.tvIdCidadeLista, R.id.tvNomeCidade, R.id.tvSiglaEstado };

		Cursor cursor = jpdroid.rawQuery(
		    "SELECT CIDADE._ID, CIDADE.NOME, ESTADO.SIGLA FROM CIDADE INNER JOIN ESTADO ON (CIDADE.ID_ESTADO = ESTADO._ID)",
		    null);

		SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(this, R.layout.activity_lista_cidades, cursor, columns,
		    to, 0);

		spCidade.setAdapter(dataAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.endereco, menu);
		return true;
	}

	public void btnSalvarEnderecoClicked(final View v) {

		endereco.setRua(etRua.getText().toString());
		endereco.setBairro(etBairro.getText().toString());
		if (etNumero.getText().toString().length() > 0) {
			endereco.setNumero(Long.parseLong(etNumero.getText().toString()));
		}
		Cursor crCidade = (Cursor) spCidade.getSelectedItem();
		endereco.setId_Cidade(crCidade.getLong(crCidade.getColumnIndex("_id")));
		endereco.setNomeCidade(crCidade.getString(crCidade.getColumnIndex("nome")));
		endereco.setPrincipal(chkPrincipal.isChecked());

		Intent it = new Intent();

		Bundle bundle = new Bundle();
		bundle.putSerializable("endereco", (Serializable) endereco);

		it.putExtras(bundle);
		it.putExtra("posicao", posicao);
		setResult(RESULT_OK, it);
		finish();
	}

}

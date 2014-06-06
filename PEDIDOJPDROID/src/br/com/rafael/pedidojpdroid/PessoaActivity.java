package br.com.rafael.pedidojpdroid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import br.com.rafael.jpdroid.core.Jpdroid;
import br.com.rafael.jpdroid.exceptions.JpdroidException;
import br.com.rafael.jpdroid.util.JpdroidConverter;
import br.com.rafael.pedidojpdroid.entity.Contato;
import br.com.rafael.pedidojpdroid.entity.Endereco;
import br.com.rafael.pedidojpdroid.entity.Pessoa;

public class PessoaActivity extends TabActivity {

	private static final int ADD_ENDERECO = 1;
	private static final int ADD_CONTATO = 2;
	private static final int ADD_FOTO = 3;

	private Pessoa pessoa;
	private List<Endereco> endereco = new ArrayList<Endereco>();
	private List<Contato> contato = new ArrayList<Contato>();

	private Jpdroid jpdroid;

	private EditText etNome;
	private ListView lvContatos;
	private ListView lvEnderecos;
	private ImageView ivFoto;
	private long _id = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pessoa);

		jpdroid = Jpdroid.getInstance();

		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);

		TabSpec descritor = getTabHost().newTabSpec("tag1");
		descritor.setContent(R.id.cliente);
		descritor.setIndicator("Cliente", getResources().getDrawable(R.drawable.pessoa));
		getTabHost().addTab(descritor);

		descritor = getTabHost().newTabSpec("tag2");
		descritor.setContent(R.id.endereco);
		descritor.setIndicator("Endereço", getResources().getDrawable(R.drawable.endereco));
		getTabHost().addTab(descritor);

		descritor = getTabHost().newTabSpec("tag3");
		descritor.setContent(R.id.contatos);
		descritor.setIndicator("Contato", getResources().getDrawable(R.drawable.contato));
		getTabHost().addTab(descritor);

		getTabHost().setCurrentTab(0);

		etNome = (EditText) findViewById(R.id.etNomePessoa);
		lvContatos = (ListView) findViewById(R.id.contatos);
		lvContatos.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Contato con = contato.get(position);
				if (con.getTipo().equals("Celular") || con.getTipo().equals("Telefone")) {
					String uri = "tel:" + con.getContato().trim();
					Intent intent = new Intent(Intent.ACTION_CALL);
					intent.setData(Uri.parse(uri));
					startActivity(intent);
				} else if (con.getTipo().equals("Email")) {
					Intent email = new Intent(Intent.ACTION_SEND);
					email.putExtra(Intent.EXTRA_EMAIL, new String[] { con.getContato() });
					email.putExtra(Intent.EXTRA_SUBJECT, "Assunto");
					email.putExtra(Intent.EXTRA_TEXT, "Escreva sua mensagem aqui...");
					email.setType("message/rfc822");
					startActivity(Intent.createChooser(email, "Enviar com :"));
				}
			}
		});

		ivFoto = (ImageView) findViewById(R.id.ivFoto);
		ivFoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(i, ADD_FOTO);
				getTabHost().setCurrentTab(0);

			}
		});

		lvEnderecos = (ListView) findViewById(R.id.endereco);

		Intent i = getIntent();
		_id = i.getLongExtra("_id", 0);
		if (_id > 0) {
			pessoa = (Pessoa) jpdroid.getObjects(Pessoa.class, "_id = " + _id, true).get(0);
			contato = pessoa.getContato();
			endereco = pessoa.getEndereco();
			if (pessoa.getFoto() != null) {
				ivFoto.setImageBitmap(pessoa.getFoto());
			}
			etNome.setText(pessoa.getNome());

			fillContato();
			fillEndereco();
		} else {
			pessoa = new Pessoa();
		}
		tabHost.setOnTabChangedListener(onOnTabChangeListener);

		// Oculta teclado ao criar a Activity
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public OnTabChangeListener onOnTabChangeListener = new OnTabChangeListener() {
		@Override
		public void onTabChanged(String tabId) {
			if (tabId == "tag2") {
				registerForContextMenu(lvEnderecos);
			} else if (tabId == "tag3") {
				registerForContextMenu(lvContatos);
			}
		}
	};

	public void onClickSalvarPessoa(View v) {

		try {
			pessoa.setNome(etNome.getText().toString());
			pessoa.setFoto(loadBitmapFromView(ivFoto));
			pessoa.setContato(contato);

			pessoa.setEndereco(endereco);

			jpdroid.persist(pessoa);
			finish();

		} catch (JpdroidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void fillEndereco() {
		String[] columns = new String[] { "rua", "numero", "bairro", "nomeCidade" };

		int[] to = new int[] { R.id.tvEnderecoListaEndereco, R.id.tvNumeroValor, R.id.tvBairroValor, R.id.tvCidadeValor };

		MatrixCursor matrixCursor = JpdroidConverter.toMatrixCursor(endereco, false);

		SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(this, R.layout.activity_lista_endereco, matrixCursor,
		    columns, to, 0);

		lvEnderecos.setAdapter(dataAdapter);

	}

	private void fillContato() {
		String[] columns = new String[] { "tipo", "contato" };

		int[] to = new int[] { R.id.tvTipoListaContatoValor, R.id.tvContatoListaContatoValor };

		MatrixCursor matrixCursor = JpdroidConverter.toMatrixCursor(contato);

		SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(this, R.layout.activity_lista_contato, matrixCursor,
		    columns, to, 0);

		lvContatos.setAdapter(dataAdapter);
	}

	private void DeleteContato(int position) {
		Contato del = contato.get(position);
		if (del.get_id() > 0) {
			jpdroid.delete(del);
		}
		contato.remove(position);
		fillContato();
	}

	private void DeleteEndereco(int position) {
		Endereco del = endereco.get(position);
		if (del.get_id() > 0) {
			jpdroid.delete(del);
		}
		endereco.remove(position);
		fillEndereco();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pessoa, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.addContatos) {
			Intent i = new Intent(this, ContatoActivity.class);
			i.putExtra("posicao", 0);
			startActivityForResult(i, ADD_CONTATO);
			getTabHost().setCurrentTab(2);
			return true;
		} else if (item.getItemId() == R.id.addEndereco) {
			Intent i = new Intent(this, EnderecoActivity.class);
			i.putExtra("posicao", 0);
			startActivityForResult(i, ADD_ENDERECO);
			getTabHost().setCurrentTab(1);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// menu.setHeaderTitle(getString(R.string.));
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_tabpage_pessoa, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.editarTabPage:
				if (getTabHost().getCurrentTab() == 1) {
					Intent it = new Intent(this, EnderecoActivity.class);

					Bundle bundle = new Bundle();
					bundle.putSerializable("endereco", (Serializable) endereco.get(info.position));

					it.putExtras(bundle);
					it.putExtra("posicao", info.position);

					startActivityForResult(it, ADD_ENDERECO);
				} else {
					Intent it = new Intent(this, ContatoActivity.class);

					Bundle bundle = new Bundle();
					bundle.putSerializable("contato", (Serializable) contato.get(info.position));

					it.putExtras(bundle);
					it.putExtra("posicao", info.position);

					startActivityForResult(it, ADD_CONTATO);
				}
				break;
			case R.id.excluirTabPage:
				if (getTabHost().getCurrentTab() == 1) {
					DeleteEndereco(info.position);
				} else {
					DeleteContato(info.position);
				}
				break;
			default:
				return super.onContextItemSelected(item);
		}
		return super.onContextItemSelected(item);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADD_CONTATO) {
			if (resultCode == Activity.RESULT_OK && data != null) {
				Contato novo = (Contato) data.getExtras().getSerializable("contato");
				if (novo.get_id() == 0) {
					contato.add(novo);
				} else {
					contato.set(data.getIntExtra("posicao", 0), novo);
				}
				fillContato();
			}
		}
		if (requestCode == ADD_ENDERECO) {
			if (resultCode == Activity.RESULT_OK && data != null) {
				Endereco novo = (Endereco) data.getExtras().getSerializable("endereco");
				if (novo.get_id() == 0) {
					endereco.add(novo);
				} else {
					endereco.set(data.getIntExtra("posicao", 0), novo);
				}
				fillEndereco();
			}
		}
		if (requestCode == ADD_FOTO) {
			if (resultCode == RESULT_OK && data != null) {
				Bundle extras = data.getExtras();
				Bitmap bmp = (Bitmap) extras.get("data");
				ivFoto.setImageBitmap(bmp);
			}
		}

	}

	public static Bitmap loadBitmapFromView(View v) {
		Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
		v.draw(c);
		return b;
	}
}

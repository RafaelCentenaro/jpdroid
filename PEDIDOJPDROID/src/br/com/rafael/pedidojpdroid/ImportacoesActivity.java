package br.com.rafael.pedidojpdroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import br.com.rafael.jpdroid.core.Jpdroid;
import br.com.rafael.jpdroid.enums.ScriptPath;

public class ImportacoesActivity extends Activity {

	private Jpdroid dataBase;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_importacoes);
		dataBase = Jpdroid.getInstance();
	}

	public void onClickAtualizarProduto(View v){
		if(dataBase.importSqlScript(ScriptPath.SdCard, "produto.sql") > 0){
			 Toast.makeText(getBaseContext(), "Importação realizada com sucesso!", Toast.LENGTH_LONG).show();
		}else{
			 Toast.makeText(getBaseContext(), "Ocorreu uma falha ao importar.Verifique no cartão sd se existe o arquivo 'produto.sql'.", Toast.LENGTH_SHORT).show();
		}
	}
	public void onClickAtualizarCidade(View v){
		if(dataBase.importSqlScript(ScriptPath.SdCard, "cidade.sql") > 0){
			 Toast.makeText(getBaseContext(), "Importação realizada com sucesso!", Toast.LENGTH_LONG).show();
		}else{
			 Toast.makeText(getBaseContext(), "Ocorreu uma falha ao importar. Verifique no cartão sd se existe o arquivo 'cidade.sql'.", Toast.LENGTH_SHORT).show();
		}
	}
	public void onClickAtualizarPessoa(View v){
		if(dataBase.importSqlScript(ScriptPath.SdCard, "pessoa.sql") > 0){
			 Toast.makeText(getBaseContext(), "Importação realizada com sucesso!", Toast.LENGTH_LONG).show();
		}else{
			 Toast.makeText(getBaseContext(), "Ocorreu uma falha ao importar. Verifique no cartão sd se existe o arquivo 'pessoa.sql'.", Toast.LENGTH_SHORT).show();
		}
	}

}

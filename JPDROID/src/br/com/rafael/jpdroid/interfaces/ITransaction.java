package br.com.rafael.jpdroid.interfaces;

public interface ITransaction {
	/**
	 * Inicia a transação
	 */
	void begin();

	/**
	 * Efetiva alterações ocorridas durante a transação.
	 */
	void commit();
	/**
	 * Finaliza a transação.
	 */
	void end();
}

package com.example.controleagua

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Gerenciador de persistência local utilizando SharedPreferences.
 * Responsável por salvar a meta, o consumo diário e verificar a virada de dia.
 */
class PreferencesHelper(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "controle_agua_prefs"
        private const val KEY_META = "meta_diaria_ml"
        private const val KEY_CONSUMO = "consumo_diario_ml"
        private const val KEY_ULTIMA_DATA = "ultima_data_registro"

        // Meta diária padrão inicial: 2000 ml (2 litros)
        const val META_PADRAO = 2000
    }

    init {
        // Ao inicializar o helper, verifica se o dia mudou para reiniciar o consumo se necessário
        verificarEAtualizarData()
    }

    /**
     * Verifica se a data atual é diferente da última data registrada no SharedPreferences.
     * Se for um novo dia, reinicia o consumo para 0 ml sem alterar a meta configurada pelo usuário.
     */
    fun verificarEAtualizarData() {
        val dataAtual = obterDataAtualIso()
        val ultimaDataSalva = prefs.getString(KEY_ULTIMA_DATA, null)

        if (ultimaDataSalva == null) {
            // Primeiro acesso: grava a data atual e inicia consumo com 0
            prefs.edit()
                .putString(KEY_ULTIMA_DATA, dataAtual)
                .putInt(KEY_CONSUMO, 0)
                .apply()
        } else if (ultimaDataSalva != dataAtual) {
            // Mudança de dia detectada: zera o consumo diário mantendo a meta intacta
            prefs.edit()
                .putString(KEY_ULTIMA_DATA, dataAtual)
                .putInt(KEY_CONSUMO, 0)
                .apply()
        }
    }

    /**
     * Retorna a meta configurada em ml. Padrão: 2000 ml.
     */
    fun getMeta(): Int {
        return prefs.getInt(KEY_META, META_PADRAO)
    }

    /**
     * Salva uma nova meta diária informada pelo usuário.
     */
    fun salvarMeta(novaMeta: Int) {
        if (novaMeta > 0) {
            prefs.edit().putInt(KEY_META, novaMeta).apply()
        }
    }

    /**
     * Retorna o total de água consumido no dia corrente em ml.
     */
    fun getConsumo(): Int {
        verificarEAtualizarData()
        return prefs.getInt(KEY_CONSUMO, 0)
    }

    /**
     * Adiciona uma quantidade de ml ao consumo do dia corrente.
     * Retorna o novo total consumido.
     */
    fun adicionarConsumo(quantidade: Int): Int {
        verificarEAtualizarData()
        if (quantidade <= 0) return getConsumo()

        val consumoAtual = getConsumo()
        val novoConsumo = consumoAtual + quantidade
        prefs.edit().putInt(KEY_CONSUMO, novoConsumo).apply()
        return novoConsumo
    }

    /**
     * Calcula a quantidade restante para atingir a meta (nunca menor que 0).
     */
    fun getRestante(): Int {
        val meta = getMeta()
        val consumo = getConsumo()
        val restante = meta - consumo
        return if (restante > 0) restante else 0
    }

    /**
     * Calcula o percentual da meta alcançado (pode passar de 100%).
     */
    fun getPercentual(): Int {
        val meta = getMeta()
        if (meta <= 0) return 0
        val consumo = getConsumo()
        return ((consumo.toDouble() / meta.toDouble()) * 100).toInt()
    }

    /**
     * Retorna a data de hoje no formato YYYY-MM-DD para controle interno de data.
     */
    private fun obterDataAtualIso(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formato.format(Date())
    }

    /**
     * Retorna a data de hoje no formato amigável para exibição (ex: 22/10/2026).
     */
    fun obterDataFormatadaExibicao(): String {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formato.format(Date())
    }
}

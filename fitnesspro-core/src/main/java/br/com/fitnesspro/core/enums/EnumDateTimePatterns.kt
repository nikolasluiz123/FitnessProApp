package br.com.fitnesspro.core.enums

/**
 * Enumeração que define padrões de formato para datas e horas.
 *
 * Esta enumeração fornece padrões de formato para formatar datas e horas em strings.
 *
 * @property pattern O padrão de formato para datas e horas.
 * @constructor Cria uma instância da enumeração com o padrão de formato fornecido.
 *
 * @author Nikolas Luiz Schmitt
 */
enum class EnumDateTimePatterns(val pattern: String) {

    /**
     * Padrão de formato para datas no formato "dd/MM/yyyy".
     */
    DATE("dd/MM/yyyy"),

    DATE_SQLITE("yyyy-MM-dd"),

    /**
     * Padrão de formato para datas no formato "ddMMyyyy".
     *
     * Normalmente utilizado para formatar as datas nos campos que
     * possuem um VisualTransformation aplicados pois no retorno do texto
     * ele vem sem as barras da data.
     */
    DATE_ONLY_NUMBERS("ddMMyyyy"),

    /**
     * Padrão de formato para horas no formato "HH:mm".
     */
    TIME("HH:mm"),

    TIME_ONLY_NUMBERS("HHmm"),

    /**
     * Padrão de formato para datas e horas no formato "dd/MM/yyyy HH:mm".
     */
    DATE_TIME("dd/MM/yyyy HH:mm"),

    DATE_TIME_SHORT("dd/MM/yy HH:mm"),

    DAY_MONTH_DATE_TIME_SHORT("dd/MM HH:mm"),

    DATE_TIME_SQLITE("yyyy-MM-dd HH:mm"),

    DATE_TIME_FILE_NAME("dd_MM_yyyy_HHmmss"),

    MONTH_YEAR("MMMM 'de' yyyy"),

    BACKUP_DB_FILE_NAME("dd_MM_yyyy_HH_mm_ss_SSS"),
}
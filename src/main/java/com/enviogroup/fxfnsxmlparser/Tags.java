package com.enviogroup.fxfnsxmlparser;

public interface Tags {
    //    String FILE = "Файл";
//    String DOCUMENT = "Документ";
//    String SV_SCH_FACT = "СвСчФакт";
//    String ID_SV = "ИдСв";
//    String SV_JUL_UCH = "СвЮЛУч";
    String NAIM_ORG = "НаимОрг";
    String INN = "ИННЮЛ";
    String KPP = "КПП";
    String COD_STR = "КодСтр";
    String ADR_TEXT = "АдрТекст";
    String NAIM_OSN = "НаимОсн";
    String NOM_OSN = "НомОсн";
    String DATE_OSN = "ДатаОсн";
    String OSN_PER = "ОснПер";
    String SV_LIC_PER = "СвЛицПер";
    String SV_TRAN_GRUZ = "СвТранГруз";
    String GRUZ_SV_YUL_UCH_PATH = "/Файл/Документ/СвСчФакт/ГрузПолуч/ИдСв/СвЮЛУч";
    String GRUZ_ADDRESS_PATH = "/Файл/Документ/СвСчФакт/ГрузПолуч/Адрес/АдрИнф";
    String OSN_PER_PATH = "/Файл/Документ/СвПродПер/СвПер/ОснПер";
    String SV_PER_PATH = "/Файл/Документ/СвПродПер/СвПер";
    String SV_LIC_PER_PATH = "/Файл/Документ/СвПродПер/СвПер/СвЛицПер";
    String GRUZ_OTPR_ID_PATH = "/Файл/Документ/СвСчФакт/ГрузОт/ГрузОтпр/ИдСв/СвЮЛУч";
    String GRUZ_OTPR_ADDRESS_PATH = "/Файл/Документ/СвСчФакт/ГрузОт/ГрузОтпр/Адрес/АдрИнф";
    String TRAN_GRUZ_PATH = "/Файл/Документ/СвПродПер/СвПер/ТранГруз";
    String INN_REGEX = "^\\d{10}$";
    String KPP_REGEX = "^\\d{9}$";
    String DATE_REGEX = "^(0[1-9]|[12][0-9]|3[01]).(0[1-9]|1[12]).([1-2]\\d{3})$";

}

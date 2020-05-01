package com.ookiisoftware.protips.auxiliar;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.View;

import com.ookiisoftware.protips.BuildConfig;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.BatepapoActivity;
import com.ookiisoftware.protips.activity.MainActivity;

public class Constantes {

    public static final int APP_VERSAO = 2;

    //===================== Conversas
    public static final String CONVERSA_CONTATO_ID = "conversa_contato_id";
    public static final String CONVERSA_CONTATO_NOME = "conversa_contato_nome";
    public static final String CONVERSA_CONTATO_EMAIL = "conversa_contato_email";
    public static final String CONVERSA_CONTATO_FOTO = "conversa_contato_foto";

    public static final int CONVERSA_MENSAGEM_LIDA = 2;
    public static final int CONVERSA_MENSAGEM_ENVIADA = 1;
    public static final int CONVERSA_MENSAGEM_NAO_ENVIADA = 0;

    public static class formats {
        public static final String TELEFONE = "+NN (NN) NNNNN-NNNN";
        public static final String CPF = "NNN.NNN.NNN-NN";
        public static final String DATA = "NN/NN/NNNN";
    }

    public static class firebase {
        public static class child {
            public static final String IDENTIFICADOR = "identificadores";
            public static final String USUARIO = "usuarios";
            public static final String CONTATO = "contatos";
            public static final String DADOS = "dados";
            public static final String CONVERSAS = "conversas";

            public static final String PERFIL = "perfil";
            public static final String POSTES = "postes";
            public static final String TIPSTERS = "tipsters";
            public static final String PUNTERS = "punters";
            public static final String PUNTERS_PENDENTES = "puntersPendentes";
            public static final String TELEFONE = "telefone";

            public static final String SOLICITACAO_NOVO_TIPSTER = "solicitacao_novo_tipster";
            public static final String BOM = "bom";
            public static final String RUIM = "ruim";
            public static final String ESPORTES = "esportes";
            public static final String MERCADOS = "mercados";
            public static final String BLOQUEADO = "bloqueado";
            public static final String ADMINISTRADORES = "administradores";
            public static final String VERSAO = "versao";
            public static final String APP = "app";
        }
    }

    public static class user {
        public static class logado {
            public static final String NOME = "usuario_logado_nome";
            public static final String TIPNAME = "usuario_logado_tipname";
            public static final String EMAIL = "usuario_logado_email";
            public static final String CATEGORIA = "usuario_logado_categoria";
            public static final String FOTO = "usuario_logado_foto";
            public static final String ULTIMO_EMAIL = "usuario_logado_ultimo_email";
        }
        public static class conversa {

        }
    }

    public static class intent {
        public static final String PRIMEIRO_LOGIN = "primeiroLogin";
        public static final String USER_ID = "user_id";
        public static final String PAGE_SELECT = "page_select";
        public static final String EMAIL = "email";
        public static final String IS_GERENCIA = "is_gerencia";
    }

    public static class presset {
        public static final String IS_GERENTE = "is_gerente";
    }

    //============================== SQLite
    public static final int SQLITE_BANCO_DE_DADOS_VERSAO = 1;
    public static final String SQLITE_BANCO_DE_DADOS = "protips_db" ;
    public static final String SQLITE_TABELA_CONVERSAS = "conversas";
    public static final String SQLITE_TABELA_MENSAGENS = "mensagens";


    public static class classes {
        public static class activites {
            //================================== Classes com ViewPager
            public static final String MAIN = "MainActivity";
            public static final String BATEPAPO = "BatepapoActivity";
            public static final String GERENCIA = "GerenciaActivity";
        }
    }

    //=============================================== Permissões
    public static final int REQUEST_PERMISSION_STORANGE = 101;
    public static final int REQUEST_PERMISSION_CAMERA = 102;

    //================================= Eventos de deslize e cliques
    @SuppressLint("StaticFieldLeak")
    public static View item_clicado;

    public static boolean SELECIONAR_ITEM;

    public static final int LONGCLICK = 300;
    public static final int DOUBLETAP = 500;
    public static final int SWIPE_RADIO_LIMITE = 10;
    public static final int SWIPE_RANGE_LIMITE = 10;

    //============================================== Identificador dos fragmentos
    public static final String FRAGMENT = "id_fragment";
    public static final String FRAGMENT_EDIT = "EditarPerfil";
    public static final String FRAGMENT_PREFERENCIAS = "Preferencias";

    public static class notification {
        public static AudioAttributes audioAttributes = new AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();

        public static final long[] VIBRATION = {0L, 500L};
        public static final Uri SOUND_CUSTON = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + BuildConfig.APPLICATION_ID +  "/" + R.raw.notification_sound);
        public static final Uri SOUND_DEFAULT = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }
    //=======================================
}

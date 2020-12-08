package com.ookiisoftware.protips.auxiliar;

import android.content.ContentResolver;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;

import com.ookiisoftware.protips.BuildConfig;
import com.ookiisoftware.protips.R;

public class Const {

    static final int APP_VERSAO = 17;

    public static class user {
        public static class logado {
            public static final String NOME = "usuario_logado_nome";
            public static final String TIPNAME = "usuario_logado_tipname";
            public static final String EMAIL = "usuario_logado_email";
            public static final String FOTO = "usuario_logado_foto";
            public static final String ULTIMO_EMAIL = "usuario_logado_ultimo_email";

            public static final String ID = "usuario_logado_categoria";
            public static final String TELEFONE = "usuario_logado_categoria";
            public static final String PRIVADO = "usuario_logado_categoria";
            public static final String NASCIMENTO = "usuario_logado_categoria";
            public static final String BLOQUEADO = "usuario_logado_categoria";
            public static final String ENDERECO = "usuario_logado_categoria";
            public static final String INFO = "usuario_logado_categoria";
        }
        public static class conversa {
            public static final int MENSAGEM_LIDA = 2;
            public static final int MENSAGEM_ENVIADA = 1;
            public static final int MENSAGEM_NAO_ENVIADA = 0;
        }
    }

    public static class files {
//        public static final String PUNTER_JSON = "4646753466.json";
        public static final String USER_JSON = "46434567.json";
    }

    public static class intent {
        public static final String PRIMEIRO_LOGIN = "primeiroLogin";
        public static final String USER_ID = "user_id";
        public static final String USER_NOME = "user_id";
        public static final String USER_FOTO = "user_id";
        public static final String PAGE_SELECT = "page_select";
        public static final String EMAIL = "email";
        public static final String IS_GERENCIA = "is_gerencia";
        public static final String URL_LINK = "url_link";
    }

    public static class sqlite {
        public static final int SQLITE_BANCO_DE_DADOS_VERSAO = 1;
        public static final String SQLITE_BANCO_DE_DADOS = "protips_db";
        public static final String SQLITE_TABELA_CONVERSAS = "conversas";

        public static final String SQLITE_TABELA_MENSAGENS = "mensagens";
    }

    public static class formats {
        public static final String TELEFONE = "+NN (NN) NNNNN-NNNN";
        public static final String CPF = "NNN.NNN.NNN-NN";
        public static final String DATA = "NN/NN/NNNN";
    }

    public static class presset {
        public static final String IS_GERENTE = "is_gerente";
    }

    public static class classes {
        public static class activites {
            //================================== Classes com ViewPager
            public static final String MAIN = "MainActivity";
            public static final String BATEPAPO = "BatepapoActivity";
            public static final String GERENCIA = "GerenciaActivity";
            public static final String PERFIL_TIPSTER = "PerfilTipsterActivity";
        }
        public static class fragments {
            public static class pagerPosition {
                public static final int FEED = 0;
                public static final int TIPSTERS = 1;
                public static final int PERFIL = 2;
                public static final int NOTIFICATIONS = 3;

                public static final int INICIO = 0;
                public static final int TIPSTER_SOLICITACAO = 1;
            }
            public static final String PERFIL = "PerfilFragment";

            //============================================== Identificador dos fragmentos
//            public static final String FRAGMENT = "id_fragment";
//            public static final String FRAGMENT_EDIT = "EditarPerfil";
//            public static final String FRAGMENT_PREFERENCIAS = "Preferencias";
        }
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
            public static final String POSTES_PERFIL = "post_perfil";

            public static final String SEGUIDORES_PENDENTES = "seguidoresPendentes";
            public static final String TELEFONE = "telefone";

            public static final String SOLICITACAO_NOVO_TIPSTER = "solicitacao_novo_tipster";
            public static final String SEGUIDORES = "seguidores";
            public static final String SEGUINDO = "seguindo";
            public static final String BOM = "bom";
            public static final String RUIM = "ruim";
            public static final String ESPORTES = "esportes";
            public static final String LINHAS = "linhas";
            //Use LINHAS
            @Deprecated
            public static final String MERCADOS = "mercados";
            public static final String BLOQUEADO = "bloqueado";
            public static final String ADMINISTRADORES = "administradores";
            public static final String VERSAO = "versao";
            public static final String APP = "app";
            public static final String TIPSTER = "tipster";
            public static final String TOKENS = "token";
            public static final String MESSAGES = "messages";
            public static final String AUTO_COMPLETE = "auto_complete";
            public static final String CAMPEONATOS = "campeonatos";
        }
    }

    public static class oneSignal {
        public static class tag {
            public static final String FIREBASE_ID = "firebase_id";
        }
        public static class request {
            public static final String CONTENT_TYPE = "Content-Type";
            public static final String AUTORIZACAO = "Authorization";
        }
    }

    public static class permissions {
        public static final int STORANGE = 101;
        public static final int CAMERA = 102;
    }

    public static class notification {

        public static final String CHANNEL_ID_DEFAULT = "channel_id_default";
        public static final String CHANNEL_ID_UPDATE_APP = "channel_id_update_app";
        public static final String CHANNEL_NOME = "channel_nome";
        public static final String CHANNEL_DESCRICAO = "channel_descricao";
        public static class action {
            public static final String OPEN_MAIN = "action_open_main_activity";
            public static final String OPEN_NOTIFICATION = "action_open_notifications";
        };

        public static class id {
            public static final String NOVO_PUNTER_PENDENTE = "235345";
            public static final String NOVO_PUNTER_ACEITO = "56462";
            public static final String NOVO_POST = "573425";
            public static final String TIPSTER_SOLICITACAO = "65645";

            public static final String SENDER = "protips_msg_sender";
        }
        public static class tag {
            public static final String TITLE = "protips_msg_title";
            public static final String MESSAGE = "protips_msg_,essage";
            public static final String ACTION = "protips_msg_action";
        }

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

    //================================= Eventos de deslize e cliques
    public static final int LONGCLICK = 300;
    public static final int DOUBLETAP = 500;
    public static final int SWIPE_RADIO_LIMITE = 10;
    public static final int SWIPE_RANGE_LIMITE = 10;

}

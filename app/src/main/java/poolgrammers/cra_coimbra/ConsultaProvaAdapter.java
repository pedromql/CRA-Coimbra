package poolgrammers.cra_coimbra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import poolgrammers.cra_coimbra.Util.InfoProvaItem;
import poolgrammers.cra_coimbra.Util.SessionItem;

/**
 * Created by pedromql on 15/12/2016.
 */

public class ConsultaProvaAdapter extends ArrayAdapter<InfoProvaItem> {

    public ConsultaProvaAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ConsultaProvaAdapter(Context context, int resource, List<InfoProvaItem> items) {
        super(context, resource, items);
    }

//    @Override
//    public int getViewTypeCount() {
//        return 2;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (position == 0) {
//            return 1;
//        }
//        else {
//            return 2;
//        }
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null && position == 0) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.info_provas, null);
        }
        else if (v == null && position > 0) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.tabela_sessoes, null);
        }

        final InfoProvaItem infoProvaItem = getItem(position);

        if (infoProvaItem != null) {
            if (position == 0) {
                TextView modalidade = (TextView) v.findViewById(R.id.input_modalidade);
                TextView regulamento = (TextView) v.findViewById(R.id.input_regulamento);
                TextView local = (TextView) v.findViewById(R.id.input_local);
                TextView responsavelCra = (TextView) v.findViewById(R.id.input_responsavel_cra);
                TextView juizArbitro = (TextView) v.findViewById(R.id.input_juiz_arbitro);

                if (modalidade != null) {
                    modalidade.setText(infoProvaItem.getModalidade());
                }

                if (regulamento != null) {
                    regulamento.setText(infoProvaItem.getRegulamento());
                }

                if (local != null) {
                    local.setText(infoProvaItem.getLocal());
                }

                if (responsavelCra != null) {
                    responsavelCra.setText(infoProvaItem.getResponsavelCra());
                }

                if (juizArbitro != null) {
                    juizArbitro.setText(infoProvaItem.getJuizArbitro());
                }
            }
            else if (position > 0) {
                TextView sessao = (TextView) v.findViewById(R.id.session_number_info_prova);
                TextView data = (TextView) v.findViewById(R.id.session_date_info_prova);
                TextView hora = (TextView) v.findViewById(R.id.session_time_info_prova);

                if (sessao != null) {
                    String sessao_text = "Sessão " + (position);
                    sessao.setText(sessao_text);
                }

                if (data != null) {
                    data.setText(infoProvaItem.getData());
                }

                if (hora != null) {
                    hora.setText(infoProvaItem.getHora());
                }
            }

        }

        return v;
    }

}

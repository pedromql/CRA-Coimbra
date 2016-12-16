package poolgrammers.cra_coimbra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import poolgrammers.cra_coimbra.Util.SessionItem;

/**
 * Created by pedromql on 15/12/2016.
 */

public class SessionAdapter extends ArrayAdapter<SessionItem> {

    public SessionAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public SessionAdapter(Context context, int resource, List<SessionItem> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.session_item, null);
        }

        final SessionItem session = getItem(position);

        if (session != null) {
            TextView sessionNumber = (TextView) v.findViewById(R.id.session_number);
            TextView sessionDate = (TextView) v.findViewById(R.id.session_date);
            TextView sessionTime = (TextView) v.findViewById(R.id.session_time);
            Switch sessionSwitch = (Switch) v.findViewById(R.id.session_switch);

            if (sessionNumber != null) {
                String sessao = "Sessão " + (position+1);
                sessionNumber.setText(sessao);
            }

            if (sessionDate != null) {
                sessionDate.setText(session.getData());
            }

            if (sessionTime != null) {
                sessionTime.setText(session.getTempo());
            }

            if (sessionSwitch != null) {
                sessionSwitch.setChecked(session.getChecked());
            }

            sessionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    session.setChecked(b);
                }
            });

        }

        return v;
    }

}

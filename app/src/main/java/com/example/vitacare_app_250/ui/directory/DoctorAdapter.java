package com.example.vitacare_app_250.ui.directory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.data.model.Doctor;
import com.example.vitacare_app_250.data.model.Specialty;
import com.example.vitacare_app_250.databinding.ItemDoctorBinding;

/**
 * Binds doctors to the directory list.
 *
 * <p>A {@link ListAdapter}, so updates diff against the previous list and
 * animate the rows that actually changed. The old adapter held a mutable
 * {@code ArrayList} that the activity cleared and refilled in place, then
 * called {@code notifyDataSetChanged()} — which rebinds every visible row,
 * drops scroll position and kills any in-flight animation. With a live
 * listener firing on each keystroke's worth of remote change, that was visible.
 */
final class DoctorAdapter extends ListAdapter<Doctor, DoctorAdapter.DoctorViewHolder> {

    /** Row interactions, handled by the hosting activity. */
    interface Listener {
        void onDoctorClicked(@NonNull Doctor doctor);

        void onCallClicked(@NonNull Doctor doctor);
    }

    private static final DiffUtil.ItemCallback<Doctor> DIFF =
            new DiffUtil.ItemCallback<Doctor>() {
                @Override
                public boolean areItemsTheSame(@NonNull Doctor a, @NonNull Doctor b) {
                    return a.getId().equals(b.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Doctor a, @NonNull Doctor b) {
                    return a.equals(b);
                }
            };

    private final Listener listener;

    DoctorAdapter(@NonNull Listener listener) {
        super(DIFF);
        this.listener = listener;
        // Ids are stable, so RecyclerView can keep view state across updates.
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId().hashCode();
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DoctorViewHolder(ItemDoctorBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static final class DoctorViewHolder extends RecyclerView.ViewHolder {

        private final ItemDoctorBinding views;
        private final Listener listener;

        DoctorViewHolder(@NonNull ItemDoctorBinding views, @NonNull Listener listener) {
            super(views.getRoot());
            this.views = views;
            this.listener = listener;
        }

        void bind(@NonNull Doctor doctor) {
            android.content.Context context = views.getRoot().getContext();
            String displayName = context.getString(R.string.doctor_name_format, doctor.getName());

            views.name.setText(displayName);
            setOrHide(views.focus, doctor.getSpeciality());
            setOrHide(views.degree, doctor.getDegree());

            Specialty specialty = doctor.specialty();
            if (specialty == null) {
                views.specialtyChip.setVisibility(View.GONE);
            } else {
                views.specialtyChip.setVisibility(View.VISIBLE);
                views.specialtyChip.setText(specialty.labelRes());
            }

            boolean callable = !doctor.getContact().isEmpty();
            views.call.setVisibility(callable ? View.VISIBLE : View.GONE);
            views.call.setContentDescription(
                    context.getString(R.string.doctor_call_description, displayName));
            views.call.setOnClickListener(v -> listener.onCallClicked(doctor));

            views.card.setOnClickListener(v -> listener.onDoctorClicked(doctor));
            // One announcement for the row rather than four disconnected ones.
            views.card.setContentDescription(describe(displayName, doctor, specialty, context));
        }

        private static String describe(String displayName,
                                       Doctor doctor,
                                       Specialty specialty,
                                       android.content.Context context) {
            StringBuilder sb = new StringBuilder(displayName);
            if (specialty != null) {
                sb.append(". ").append(context.getString(specialty.labelRes()));
            }
            if (!doctor.getSpeciality().isEmpty()) {
                sb.append(". ").append(doctor.getSpeciality());
            }
            if (!doctor.getDegree().isEmpty()) {
                sb.append(". ").append(doctor.getDegree());
            }
            return sb.toString();
        }

        /** Shows a line only when it has content; no "No Degree Info" filler. */
        private static void setOrHide(@NonNull android.widget.TextView view, @NonNull String text) {
            if (text.isEmpty()) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
                view.setText(text);
            }
        }
    }
}

package com.example.vitacare_app_250.ui.blood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.data.model.BloodDonor;
import com.example.vitacare_app_250.databinding.ItemDonorBinding;

/** Binds donors to the Find a Donor list. */
final class DonorAdapter extends ListAdapter<BloodDonor, DonorAdapter.DonorViewHolder> {

    interface Listener {
        void onCallClicked(@NonNull BloodDonor donor);
    }

    private static final DiffUtil.ItemCallback<BloodDonor> DIFF =
            new DiffUtil.ItemCallback<BloodDonor>() {
                @Override
                public boolean areItemsTheSame(@NonNull BloodDonor a, @NonNull BloodDonor b) {
                    return a.getId().equals(b.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull BloodDonor a, @NonNull BloodDonor b) {
                    return a.equals(b);
                }
            };

    private final Listener listener;

    DonorAdapter(@NonNull Listener listener) {
        super(DIFF);
        this.listener = listener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId().hashCode();
    }

    @NonNull
    @Override
    public DonorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DonorViewHolder(ItemDonorBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static final class DonorViewHolder extends RecyclerView.ViewHolder {

        private final ItemDonorBinding views;
        private final Listener listener;

        DonorViewHolder(@NonNull ItemDonorBinding views, @NonNull Listener listener) {
            super(views.getRoot());
            this.views = views;
            this.listener = listener;
        }

        void bind(@NonNull BloodDonor donor) {
            Context context = views.getRoot().getContext();
            String group = donor.groupLabel();

            views.bloodGroup.setText(group);
            // The badge is the primary identifier; spell it out for TalkBack
            // rather than letting it read "A plus" as punctuation.
            views.bloodGroup.setContentDescription(
                    context.getString(R.string.donor_blood_group_description, group));

            views.name.setText(donor.getName());
            views.phone.setText(donor.getPhone());

            if (donor.getLocation().isEmpty()) {
                views.location.setVisibility(View.GONE);
            } else {
                views.location.setVisibility(View.VISIBLE);
                views.location.setText(donor.getLocation());
            }

            views.call.setContentDescription(
                    context.getString(R.string.donor_call_description, donor.getName()));
            views.call.setOnClickListener(v -> listener.onCallClicked(donor));

            // Tapping the row does the same as tapping Call — there is no
            // detail screen for a donor, and a dead tap target reads as broken.
            views.card.setOnClickListener(v -> listener.onCallClicked(donor));
            views.card.setContentDescription(donor.getName() + ". "
                    + context.getString(R.string.donor_blood_group_description, group) + ". "
                    + donor.getLocation());
        }
    }
}

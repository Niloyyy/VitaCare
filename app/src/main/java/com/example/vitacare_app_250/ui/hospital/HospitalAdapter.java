package com.example.vitacare_app_250.ui.hospital;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.data.model.Hospital;
import com.example.vitacare_app_250.databinding.ItemHospitalBinding;

/**
 * Binds hospitals to the availability list.
 *
 * <p>Replaces an {@code ArrayAdapter} feeding a {@code ListView}. Beyond the
 * usual reasons to prefer RecyclerView, that adapter's {@code getView} looked
 * up all four of its TextViews with {@code findViewById} on every bind — it
 * reused the convertView but not the lookups, so scrolling did the expensive
 * part of inflation anyway.
 */
final class HospitalAdapter extends ListAdapter<Hospital, HospitalAdapter.HospitalViewHolder> {

    interface Listener {
        void onHospitalClicked(@NonNull Hospital hospital);
    }

    private static final DiffUtil.ItemCallback<Hospital> DIFF =
            new DiffUtil.ItemCallback<Hospital>() {
                @Override
                public boolean areItemsTheSame(@NonNull Hospital a, @NonNull Hospital b) {
                    return a.getId().equals(b.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Hospital a, @NonNull Hospital b) {
                    return a.equals(b);
                }
            };

    private final Listener listener;

    HospitalAdapter(@NonNull Listener listener) {
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
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HospitalViewHolder(ItemHospitalBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static final class HospitalViewHolder extends RecyclerView.ViewHolder {

        private final ItemHospitalBinding views;
        private final Listener listener;

        HospitalViewHolder(@NonNull ItemHospitalBinding views, @NonNull Listener listener) {
            super(views.getRoot());
            this.views = views;
            this.listener = listener;
        }

        void bind(@NonNull Hospital hospital) {
            Context context = views.getRoot().getContext();

            views.name.setText(hospital.getName());
            if (hospital.getAddress().isEmpty()) {
                views.address.setVisibility(View.GONE);
            } else {
                views.address.setVisibility(View.VISIBLE);
                views.address.setText(hospital.getAddress());
            }

            bindStatus(context, hospital);
            bindMetric(views.bedsValue, views.bedsGroup, hospital.availableBeds());
            bindMetric(views.icuValue, views.icuGroup, hospital.availableIcu());
            views.metrics.setVisibility(hospital.hasBedData() ? View.VISIBLE : View.GONE);

            views.card.setOnClickListener(v -> listener.onHospitalClicked(hospital));
            views.card.setContentDescription(describe(context, hospital));
        }

        private void bindStatus(@NonNull Context context, @NonNull Hospital hospital) {
            @StringRes int label;
            @ColorRes int container;
            @ColorRes int text;

            switch (hospital.availability()) {
                case AVAILABLE:
                    label = R.string.hospital_status_available;
                    container = R.color.status_available_container;
                    text = R.color.status_available;
                    break;
                case LIMITED:
                    label = R.string.hospital_status_limited;
                    container = R.color.status_limited_container;
                    text = R.color.status_limited;
                    break;
                case FULL:
                    label = R.string.hospital_status_full;
                    container = R.color.status_full_container;
                    text = R.color.status_full;
                    break;
                default:
                    label = R.string.hospital_availability_unknown;
                    container = R.color.md_surface_container_high;
                    text = R.color.md_on_surface_variant;
                    break;
            }

            views.statusChip.setText(label);
            views.statusChip.setChipBackgroundColor(
                    ContextCompat.getColorStateList(context, container));
            ColorStateList textColor = ContextCompat.getColorStateList(context, text);
            if (textColor != null) {
                views.statusChip.setTextColor(textColor);
            }
        }

        /**
         * Shows a count, or hides its column when the hospital has not
         * reported one. Printing "-1" or a bare "0" for missing data would
         * misrepresent an unknown as a definite zero.
         */
        private static void bindMetric(@NonNull TextView value,
                                       @NonNull View group,
                                       int count) {
            if (count == Hospital.UNKNOWN) {
                group.setVisibility(View.GONE);
            } else {
                group.setVisibility(View.VISIBLE);
                value.setText(String.valueOf(count));
            }
        }

        private static String describe(@NonNull Context context, @NonNull Hospital hospital) {
            StringBuilder sb = new StringBuilder(hospital.getName());
            if (!hospital.getAddress().isEmpty()) {
                sb.append(". ").append(hospital.getAddress());
            }
            if (hospital.availableBeds() != Hospital.UNKNOWN) {
                sb.append(". ").append(context.getResources().getQuantityString(
                        R.plurals.hospital_beds_short,
                        hospital.availableBeds(), hospital.availableBeds()));
            }
            if (hospital.availableIcu() != Hospital.UNKNOWN) {
                sb.append(". ").append(context.getResources().getQuantityString(
                        R.plurals.hospital_icu_short,
                        hospital.availableIcu(), hospital.availableIcu()));
            }
            return sb.toString();
        }
    }
}

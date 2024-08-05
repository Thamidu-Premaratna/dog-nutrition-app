package com.app.dognutrition.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.app.dognutrition.databinding.FragmentProfileBinding;
import com.app.dognutrition.models.User;
import com.app.dognutrition.ui.LoginActivity;
import com.app.dognutrition.utils.SQLiteUtils;
import org.jetbrains.annotations.Nullable;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private User user;
    private EditText etName, etEmail, etContact, etAddress, etPaymentMethod;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        etName = binding.etName;
        etEmail = binding.etEmail;
        etContact = binding.etContact;
        etAddress = binding.etAddress;
        etPaymentMethod = binding.etPaymentMethod;

        // On mouse click update the user details according to the new values in the fields
        binding.btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("ProfileFragment: Update button clicked");
                // Get the new values from the fields
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String contact = etContact.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String paymentMethod = etPaymentMethod.getText().toString().trim();

                // Update the user details in the database
                SQLiteUtils dbHelper = new SQLiteUtils(requireContext());
                System.out.println("Name: " + name + ", Email: " + email + ", Contact: " + contact + ", Address: " + address + ", Payment Method: " + paymentMethod);
                boolean isUpdated = dbHelper.updateUserProfile(name, email, contact, address, paymentMethod);
                if (isUpdated) {
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    // Update the user object with the new values
                    User updatedUser = new User(name, email, user.getPassword(), contact, address, paymentMethod);
                    setProfileFields(updatedUser);
                } else {
                    Toast.makeText(requireContext(), "Profile update failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // On mouse click logout the user
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the shared preferences
                SharedPreferences sharedpreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.apply();

                // Redirect to the login activity
                requireActivity().finish();
                Intent i = new Intent(requireActivity(), LoginActivity.class);
                startActivity(i);
            }
        });

        return getView(root);

    }

    private @Nullable View getView(View root) {

        // Get the user email from shared preferences, use it to get the user details from the database
        // and create a User object and using that object set the text of the text views in the profile fragment
        SharedPreferences sharedpreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String email = sharedpreferences.getString("email", null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("ProfileFragment: Getting user details from database...");
                SQLiteUtils dbHelper = new SQLiteUtils(requireContext());
                Cursor cursor = dbHelper.getUserByEmail(email);
                if (cursor.getCount() == 0) {
                    System.out.println("ProfileFragment: User not found");
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(requireContext(), "User not found, Internal Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    cursor.moveToFirst();
                    int nameIndex = cursor.getColumnIndex("name");
                    int emailIndex = cursor.getColumnIndex("email");
                    int passwordIndex = cursor.getColumnIndex("password");
                    int contactIndex = cursor.getColumnIndex("contact");
                    int addressIndex = cursor.getColumnIndex("address");
                    int paymentMethodIndex = cursor.getColumnIndex("payment_method");

                    user = new User(
                            nameIndex != -1 ? cursor.getString(nameIndex) : "",
                            emailIndex != -1 ? cursor.getString(emailIndex) : "",
                            passwordIndex != -1 ? cursor.getString(passwordIndex) : "",
                            contactIndex != -1 ? cursor.getString(contactIndex) : "",
                            addressIndex != -1 ? cursor.getString(addressIndex) : "",
                            paymentMethodIndex != -1 ? cursor.getString(paymentMethodIndex) : ""
                    );
                    System.out.println("User details: " + user.getName() + ", " + user.getEmail() + ", " + user.getPhone() + ", " + user.getAddress() + ", " + user.getPaymentMethod());
                    cursor.close();

                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setProfileFields(user);
                        }
                    });
                }
            }
        }).start();

        return root;
    }


    private void setProfileFields(User user) {
        etName.setText(user.getName());
        etEmail.setText(user.getEmail());
        etContact.setText(user.getPhone());
        etAddress.setText(user.getAddress());
        etPaymentMethod.setText(user.getPaymentMethod());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
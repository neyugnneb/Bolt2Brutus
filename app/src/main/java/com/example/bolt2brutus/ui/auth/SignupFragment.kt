package com.example.bolt2brutus.ui.auth

import android.util.Log
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import com.example.bolt2brutus.R
import com.example.bolt2brutus.viewmodel.AuthUiState
import com.example.bolt2brutus.viewmodel.AuthViewModel

/** Account creation. Navigation after success is handled by [LoginActivity]. */
class SignupFragment : Fragment() {

    companion object {
        private const val tag = "SignupFragment"
    }

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(tag, "onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val usernameEditText = view.findViewById<EditText>(R.id.usernameEditText)
        val emailEditText = view.findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        val submitButton = view.findViewById<Button>(R.id.signUpSubmitButton)

        submitButton.setOnClickListener {
            authViewModel.signUp(
                emailEditText.text.toString(),
                passwordEditText.text.toString(),
                usernameEditText.text.toString(),
            )
        }

        authViewModel.uiState.observe(viewLifecycleOwner) { state ->
            submitButton.isEnabled = state !is AuthUiState.Loading
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(tag, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(tag, "onDestroyView")
    }


}

package com.announce.presenter.registration.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.announce.R
import com.announce.databinding.RegistrationFragmentBinding
import com.announce.presenter.registration.viewmodel.*

class RegistrationFragment: Fragment() {

    private lateinit var viewModel: RegistrationFragmentViewModel
    private lateinit var binding: RegistrationFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)
            .get(RegistrationFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = RegistrationFragmentBinding
            .inflate(
                inflater,
                container,
                false
            )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.btnLogin.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: State) {

        resetState()

        when(state) {
            //do nothing on init
            is InitUserState -> {}
            is ValidationErrorsState -> renderValidationErrors(state)
            is RegisteredUserState -> renderRegisteredUserState()
            is LoadingState -> renderLoadingState()
        }
    }

    private fun resetState() {
        binding.run {
            if (progressCircular.visibility != View.GONE) {
                progressCircular.visibility = View.GONE
                btnRegistrate.visibility = View.VISIBLE
            }
        }
    }

    private fun renderLoadingState() {
        binding.run {
            progressCircular.visibility = View.VISIBLE
            btnRegistrate.visibility = View.INVISIBLE
        }
    }

    private fun renderValidationErrors(state: ValidationErrorsState) {

        if(state.hasError(ValidationErrorsState.ErrorType.emailFormat)) {
            binding.txtLayoutEmail.error =
                getString(R.string.bad_email_format)
        }

        if(state.hasError(ValidationErrorsState.ErrorType.passwordLength)) {
            binding.txtLayoutPassword.error =
                getString(R.string.password_must_be_at_least_6_length)
        }
    }

    private fun renderRegisteredUserState() {
        Toast.makeText(
            requireContext(),
            "Registered",
            Toast.LENGTH_SHORT
        ).show()
    }
}
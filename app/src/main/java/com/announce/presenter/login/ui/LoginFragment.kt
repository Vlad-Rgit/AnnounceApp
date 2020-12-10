package com.announce.presenter.login.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.announce.R
import com.announce.databinding.LoginFragmentBinding
import com.announce.presenter.login.viewmodel.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginFragment: Fragment() {

    private lateinit var viewModel: LoginFragmentViewModel
    private lateinit var binding: LoginFragmentBinding

    private var currentState: State? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory(requireActivity().application))
            .get(LoginFragmentViewModel::class.java)


    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = LoginFragmentBinding.inflate(
            inflater,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.btnRegistrate.setOnClickListener {
            findNavController().navigate(
                R.id.action_loginFragment_to_registrationFragment
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeViewModel()
    }


    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: State) {

        if(state.javaClass.isInstance(currentState))
            return

        currentState = state

        resetState()

        when(state) {
            //do nothing on init
            is InitUserState -> {}
            is InvalidCredentialsState -> renderInvalidCredentials()
            is LoggedUserState -> renderLoggedUser()
            is LoadingUserState -> renderLoadingState()
        }
    }

    private fun resetState() {
        binding.run {
            if (progressCircular.visibility != View.GONE) {
                progressCircular.visibility = View.GONE
                btnLogin.visibility = View.VISIBLE
            }
        }
    }

    private fun renderLoadingState() {
        binding.run {
            progressCircular.visibility = View.VISIBLE
            btnLogin.visibility = View.INVISIBLE
        }
    }

    private fun renderInvalidCredentials() {
        Toast.makeText(
            requireContext(),
            getString(R.string.invalid_credentials),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun renderLoggedUser() {

        Toast.makeText(
            requireContext(),
            getString(R.string.succesfull_login),
            Toast.LENGTH_SHORT
        ).show()

        findNavController().navigate(
            R.id.action_loginFragment_to_mainMenuFragment
        )
    }


}
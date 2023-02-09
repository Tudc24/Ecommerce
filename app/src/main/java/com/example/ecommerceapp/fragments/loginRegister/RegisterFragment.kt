package com.example.ecommerceapp.fragments.loginRegister;


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerceapp.R
import com.example.ecommerceapp.data.User
import com.example.ecommerceapp.databinding.FragmentRegisterBinding
import com.example.ecommerceapp.util.RegisterValidation
import com.example.ecommerceapp.util.Resource
import com.example.ecommerceapp.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.withContext


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()
    private val TAG = "RegisterFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDoHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.apply {
            btnRegister.setOnClickListener {
                val user = User(
                    edtFirstName.text.toString().trim(),
                    edtLastName.text.toString().trim(),
                    edtEmail.text.toString().trim(),
                )
                val password = edtPassWord.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect() {
                when (it) {
                    is Resource.Loading -> {
//                        binding.btnRegister.startAnimation()
                        binding.progress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        Log.d("test", it.data.toString())
                        binding.progress.visibility = View.GONE
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }
                    is Resource.Error -> {
                        Log.d(TAG, it.message.toString())
                        binding.progress.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect() { validation ->
                if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edtEmail.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edtPassWord.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }

            }
        }

    }
}